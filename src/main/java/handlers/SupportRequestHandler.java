package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.SupportRequest;
import model.Team;
import model.User;
import model.dto.CreateSupportRequestDTO;
import repository.*;
import utils.DomainException;
import validators.SupportRequestValidator;

import java.time.LocalDateTime;
import java.util.List;

public class SupportRequestHandler {

    private final EntityManager em;

    private final SupportRequestValidator supportRequestValidator;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;

    public SupportRequestHandler(EntityManager em) {
        this.em = em;

        this.userRepository = new UserRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
        this.supportRequestRepository = new SupportRequestRepository(em);
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);

        this.supportRequestValidator = new SupportRequestValidator(
                userRepository,
                teamRepository,
                hackathonRepository,
                participatingTeamRepository
        );
    }

    public void createSupportRequest(Long userId, Long hackathonId, CreateSupportRequestDTO dto) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            supportRequestValidator.validate(dto, userId, hackathonId);

            Team team = teamRepository.findByMemberId(userId);
            ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathonId, team.getId());

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


    public List<SupportRequest> getSupportRequestsList(Long staffProfileId, Long hackathonId) {

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(staffProfileId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffProfileId) ||
                hackathon.getJudge().equals(staffProfileId) ||
                hackathon.getMentors().contains(staffProfileId);

        if (!isStaff) throw new DomainException("Operazione non autorizzata: non fai parte dello staff");

        return supportRequestRepository.getByHackathonId(hackathonId);
    }

    public SupportRequest getSupportRequestDetails(Long staffProfileId, Long hackathonId, Long supportRequestId) {

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(staffProfileId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffProfileId) ||
                hackathon.getJudge().equals(staffProfileId) ||
                hackathon.getMentors().contains(staffProfileId);

        if (!isStaff) throw new DomainException("Operazione non autorizzata: non fai parte dello staff");

        SupportRequest request = supportRequestRepository.getById(supportRequestId);
        if (request == null) throw new DomainException("Richiesta di supporto non trovata");

        if (!request.getHackathon().equals(hackathonId)) {
            throw new DomainException("La richiesta di supporto non appartiene all'hackathon specificato");
        }

        return request;
    }
}