package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.SupportRequest;
import model.dto.CallBookingRequest;
import model.dto.requestdto.BookSupportCallDTO;
import model.dto.requestdto.CallBookingResult;
import model.dto.requestdto.CreateSupportRequestDTO;
import model.dto.requestdto.ReplySupportRequestDTO;
import model.enums.HackathonStatus;
import model.enums.SupportRequestStatus;
import repository.*;
import utils.DomainException;
import utils.facade.ICalendarService;
import validators.SupportRequestValidator;

import java.time.LocalDateTime;
import java.util.List;

public class SupportRequestHandler {

    private final EntityManager em;
    private final SupportRequestValidator supportRequestValidator;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final ICalendarService calendarService;

    public SupportRequestHandler(EntityManager em, ICalendarService calendarService) {
        this.em = em;
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
        this.supportRequestRepository = new SupportRequestRepository(em);
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.calendarService = calendarService;
        this.supportRequestValidator = new SupportRequestValidator();
    }

    public void createSupportRequest(Long userId, Long hackathonId, CreateSupportRequestDTO dto) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            supportRequestValidator.validate(dto);
            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING) {
                throw new DomainException("Impossibile aprire un ticket: l'hackathon non è attualmente in corso");
            }
            ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndActiveMemberId(hackathonId, userId);
            if (pt == null) {
                throw new DomainException("Non sei un membro attivo di un team iscritto a questo hackathon");
            }
            SupportRequest request = new SupportRequest(
                    hackathonId,
                    pt.getId(),
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getUrgency(),
                    LocalDateTime.now()
            );
            supportRequestRepository.save(request);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void replyToSupportRequest(Long staffProfileId, Long hackathonId, Long supportRequestId, ReplySupportRequestDTO dto) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            supportRequestValidator.validate(dto);

            boolean isMentor = hackathonRepository.existsMentor(hackathonId, staffProfileId);
            if (!isMentor) {
                throw new DomainException("Operazione non autorizzata: non sei un mentore per questo hackathon");
            }

            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING) {
                throw new DomainException("L'hackathon non è attualmente in corso");
            }

            SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
            if (supportRequest == null) {
                throw new DomainException("Richiesta di supporto non trovata");
            }

            if (!supportRequest.isOpen()) {
                throw new DomainException("La richiesta di supporto non è in attesa di risposta");
            }

            supportRequest.addReply(staffProfileId, dto.getMessage(), LocalDateTime.now());

            supportRequestRepository.save(supportRequest);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void bookSupportCall(Long staffProfileId, Long hackathonId, Long supportRequestId, BookSupportCallDTO bookSupportCallDTO) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            supportRequestValidator.validate(bookSupportCallDTO);
            boolean isMentor = hackathonRepository.existsMentor(hackathonId, staffProfileId);
            if (!isMentor) {
                throw new DomainException("L'utente non è un mentore per questo hackathon");
            }
            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING) {
                throw new DomainException("L'hackathon non è in corso");
            }
            SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
            if (supportRequest == null) {
                throw new DomainException("Richiesta di supporto non trovata");
            }
            if (!supportRequest.isOpen()) {
                throw new DomainException("La richiesta di supporto non è più aperta");
            }
            Long participatingTeamId = supportRequest.getParticipatingTeam();
            ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
            if (participatingTeam == null) {
                throw new DomainException("Team partecipante non trovato");
            }
            String attendeeEmail = participatingTeam.getContactEmail();
            CallBookingRequest callBookingRequest = new CallBookingRequest(
                    staffProfileId,
                    bookSupportCallDTO.getTitle(),
                    bookSupportCallDTO.getDescription(),
                    bookSupportCallDTO.getStartsAt(),
                    bookSupportCallDTO.getDuration(),
                    attendeeEmail
            );
            CallBookingResult callBookingResult = calendarService.scheduleCall(callBookingRequest);
            if (!callBookingResult.isSuccess()) {
                throw new DomainException("Errore nella pianificazione della call: " + callBookingResult.getFailureReason());
            }
            supportRequest.scheduleCall(
                    staffProfileId,
                    bookSupportCallDTO.getStartsAt(),
                    bookSupportCallDTO.getDuration(),
                    callBookingResult.getEventId(),
                    callBookingResult.getMeetingURL()
            );
            supportRequestRepository.save(supportRequest);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

}