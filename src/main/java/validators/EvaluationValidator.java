package validators;

import model.Hackathon;
import model.StaffProfile;
import model.Submission;
import model.dto.AddEvaluationDTO;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import repository.SubmissionRepository;
import utils.DomainException;

public class EvaluationValidator {

    private final HackathonRepository hackathonRepository;
    private final StaffProfileRepository staffProfileRepository;
    private final SubmissionRepository submissionRepository;

    public EvaluationValidator(
            HackathonRepository hackathonRepository,
            StaffProfileRepository staffProfileRepository,
            SubmissionRepository submissionRepository
    ) {
        this.hackathonRepository = hackathonRepository;
        this.staffProfileRepository = staffProfileRepository;
        this.submissionRepository = submissionRepository;
    }

    public void validate(AddEvaluationDTO dto, Long staffId, Long submissionId) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");
        if (staffId == null || submissionId == null) throw new IllegalArgumentException("ID mancanti");

        if (dto.getScore() < 0 || dto.getScore() > 10)
            throw new IllegalArgumentException("Score non valido (0-10)");

        Submission submission = submissionRepository.getById(submissionId);
        if (submission == null) throw new DomainException("Submission non trovata");

        Hackathon hackathon = submission.getHackathon();

        StaffProfile staff = staffProfileRepository.getById(staffId);
        if (staff == null) throw new DomainException("Giudice non trovato");

        submission.assertBelongsToHackathon(hackathon);
        hackathon.assertInEvaluation();
        hackathon.assertJudge(staff);

        if (submission.getEvaluation() != null) {
            throw new DomainException("Già valutata");
        }
    }
}