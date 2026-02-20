package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.Submission;
import model.Team;
import model.dto.AddSubmissionDTO;
import repository.*;
import utils.DomainException;
import validators.SubmissionValidator;

import java.util.List;

public class SubmissionHandler {

    private final EntityManager em;

    private final SubmissionValidator submissionValidator;
    private final StaffProfileRepository staffProfileRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;
    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public SubmissionHandler(EntityManager em) {
        this.em = em;

        this.staffProfileRepository = new StaffProfileRepository(em);
        this.submissionRepository = new SubmissionRepository(em);
        this.userRepository = new UserRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);

        this.submissionValidator = new SubmissionValidator(
                hackathonRepository,
                userRepository,
                teamRepository,
                participatingTeamRepository,
                submissionRepository
        );
    }

    public void createSubmission(Long userId, Long hackathonId, AddSubmissionDTO dto) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            submissionValidator.validate(dto, userId, hackathonId);

            Team team = teamRepository.findByMemberId(userId);
            ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathonId, team.getId());

            Submission submission = new Submission(
                    hackathonId,
                    pt.getId(),
                    dto.getResponse(),
                    dto.getResponseURL()
            );

            submissionRepository.save(submission);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public List<Submission> getSubmissionsList(Long staffProfileId, Long hackathonId) {

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(staffProfileId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffProfileId) ||
                hackathon.getJudge().equals(staffProfileId) ||
                hackathon.getMentors().contains(staffProfileId);

        if (!isStaff) throw new DomainException("Operazione non autorizzata: non fai parte dello staff");

        return submissionRepository.findByHackathonId(hackathonId);
    }

    public Submission getSubmissionDetails(Long staffProfileId, Long hackathonId, Long submissionId) {

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(staffProfileId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffProfileId) ||
                hackathon.getJudge().equals(staffProfileId) ||
                hackathon.getMentors().contains(staffProfileId);

        if (!isStaff) throw new DomainException("Operazione non autorizzata: non fai parte dello staff");

        Submission submission = submissionRepository.getById(submissionId);
        if (submission == null) throw new DomainException("Sottomissione non trovata");

        if (!submission.getHackathon().equals(hackathonId)) {
            throw new DomainException("La sottomissione non appartiene all'hackathon selezionato");
        }

        return submission;
    }
}