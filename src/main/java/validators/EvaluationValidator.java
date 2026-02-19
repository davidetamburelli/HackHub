package validators;

import model.Hackathon;
import model.StaffProfile;
import model.Submission;
import model.dto.AddEvaluationDTO;
import model.enums.HackathonStatus;
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

        if (dto.getScore() < 0 || dto.getScore() > 10) {
            throw new IllegalArgumentException("Score non valido (0-10)");
        }

        Submission submission = submissionRepository.getById(submissionId);
        if (submission == null) {
            throw new DomainException("Submission non trovata");
        }

        StaffProfile staff = staffProfileRepository.getById(staffId);
        if (staff == null) {
            throw new DomainException("Profilo staff non trovato");
        }

        Long hackathonId = submission.getHackathon();
        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) {
            throw new DomainException("L'hackathon associato a questa submission non esiste più");
        }

        if (hackathon.getStatus() != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("L'hackathon non è attualmente in fase di valutazione");
        }

        if (!hackathon.getJudge().equals(staffId)) {
            throw new DomainException("Operazione non autorizzata: l'utente non è il giudice di questo hackathon");
        }

        if (submission.hasEvaluation()) {
            throw new DomainException("La submission è già stata valutata");
        }
    }
}