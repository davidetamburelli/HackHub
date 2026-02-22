package handlers;

import model.ParticipatingTeam;
import model.SupportRequest;
import model.dto.CallBookingRequest;
import model.dto.requestdto.BookSupportCallDTO;
import model.dto.requestdto.CallBookingResult;
import model.dto.requestdto.CreateSupportRequestDTO;
import model.dto.requestdto.ReplySupportRequestDTO;
import model.dto.responsedto.BookCallResponseDTO;
import model.dto.responsedto.SupportRequestDetailsDTO;
import model.dto.responsedto.SupportRequestSummaryDTO;
import model.enums.HackathonStatus;
import model.mappers.SupportRequestDTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.SupportRequestRepository;
import utils.DomainException;
import utils.facade.ICalendarService;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportRequestHandler {

    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final HackathonRepository hackathonRepository;
    private final ICalendarService calendarService;

    public SupportRequestHandler(
            ParticipatingTeamRepository participatingTeamRepository,
            SupportRequestRepository supportRequestRepository,
            HackathonRepository hackathonRepository,
            ICalendarService calendarService) {
        this.participatingTeamRepository = participatingTeamRepository;
        this.supportRequestRepository = supportRequestRepository;
        this.hackathonRepository = hackathonRepository;
        this.calendarService = calendarService;
    }

    @Transactional
    public void createSupportRequest(Long userId, Long hackathonId, CreateSupportRequestDTO dto) {
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
    }

    public List<SupportRequestSummaryDTO> getSupportRequests(Long staffProfileId, Long hackathonId) {
        boolean isMentor = hackathonRepository.existsMentor(hackathonId, staffProfileId);
        if (!isMentor) {
            throw new DomainException("Operazione non autorizzata: non sei un mentore dell'hackathon");
        }
        List<SupportRequest> supportRequests = supportRequestRepository.getByHackathonId(hackathonId);
        return supportRequests.stream()
                .map(SupportRequestDTOMapper::toSummary)
                .toList();
    }

    public SupportRequestDetailsDTO getSupportRequestDetails(Long staffProfileId, Long hackathonId, Long supportRequestId) {
        boolean isMentor = hackathonRepository.existsMentor(hackathonId, staffProfileId);
        if (!isMentor) {
            throw new DomainException("Operazione non autorizzata: non sei un mentore dell'hackathon");
        }

        SupportRequest supportRequest = supportRequestRepository.getByIdAndHackathonId(supportRequestId, hackathonId);
        if (supportRequest == null) {
            throw new DomainException("La richiesta di supporto non appartiene all'hackathon selezionato");
        }

        ParticipatingTeam partecipatingTeam = participatingTeamRepository.getById(supportRequest.getParticipatingTeam());
        if (partecipatingTeam == null) {
            throw new DomainException("Il partecipating team non esiste nel sistema");
        }

        return SupportRequestDTOMapper.toDetails(supportRequest, partecipatingTeam);
    }

    @Transactional
    public void replyToSupportRequest(Long staffProfileId, Long hackathonId, Long supportRequestId, ReplySupportRequestDTO dto) {
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
            throw new DomainException("La richiesta di supporto è già stata risolta");
        }

        supportRequest.addReply(staffProfileId, dto.getMessage(), LocalDateTime.now());

        supportRequestRepository.save(supportRequest);
    }

    @Transactional
    public BookCallResponseDTO bookSupportCall(Long staffProfileId, Long hackathonId, Long supportRequestId, BookSupportCallDTO bookSupportCallDTO) {
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
            throw new DomainException("La richiesta di supporto è già stata risolta");
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

        return SupportRequestDTOMapper.toBookCallResponse(supportRequest);
    }
}