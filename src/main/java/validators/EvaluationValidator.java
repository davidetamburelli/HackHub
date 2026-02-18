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

    public void validate(AddEvaluationDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");

        if (dto.getStaffProfileId() == null || dto.getHackathonId() == null || dto.getSubmissionId() == null) {
            throw new IllegalArgumentException("ID Staff, Hackathon e Submission sono obbligatori");
        }
        if (dto.getScore() < 0 || dto.getScore() > 10) {
            throw new IllegalArgumentException("Il punteggio deve essere compreso tra 0 e 10");
        }

        Hackathon hackathon = hackathonRepository.getById(dto.getHackathonId());
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile staff = staffProfileRepository.getById(dto.getStaffProfileId());
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        Submission submission = submissionRepository.getById(dto.getSubmissionId());
        if (submission == null) throw new DomainException("Submission non trovata");

        submission.assertBelongsToHackathon(hackathon);

        hackathon.isInEvaluation();

        hackathon.assertIsJudge(staff);

        if (submission.getEvaluation() != null) {
            throw new DomainException("Questa sottomissione è già stata valutata.");
        }
    }
}