package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.Submission;
import model.Team;
import model.User;
import model.dto.AddSubmissionDTO;
import repository.*;
import utils.DomainException;
import validators.SubmissionValidator;

import java.time.LocalDateTime;
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

            Hackathon hackathon = hackathonRepository.getById(hackathonId);
            User user = userRepository.getById(userId);
            Team team = teamRepository.findByMemberId(user.getId());
            ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathon.getId(), team.getId());

            Submission submission = new Submission(
                    hackathon,
                    pt,
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

        hackathon.assertStaff(staff);

        return submissionRepository.findByHackathonId(hackathonId);
    }

    public Submission getSubmissionDetails(Long staffProfileId, Long hackathonId, Long submissionId) {

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(staffProfileId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        hackathon.assertStaff(staff);

        Submission submission = submissionRepository.getById(submissionId);
        if (submission == null) throw new DomainException("Sottomissione non trovata");

        submission.assertBelongsToHackathon(hackathon);

        return submission;
    }
}