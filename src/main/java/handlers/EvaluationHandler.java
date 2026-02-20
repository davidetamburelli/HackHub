package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Submission;
import model.dto.AddEvaluationDTO;
import model.enums.HackathonStatus;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import repository.SubmissionRepository;
import validators.EvaluationValidator;
import utils.DomainException;

public class EvaluationHandler {

    private final EntityManager em;

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;

    private final EvaluationValidator evaluationValidator;

    public EvaluationHandler(EntityManager em) {
        this.em = em;
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.submissionRepository = new SubmissionRepository(em);
        this.evaluationValidator = new EvaluationValidator();
    }

    public void addEvaluation(Long staffProfileId, Long hackathonId, Long submissionId, AddEvaluationDTO dto) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            evaluationValidator.validate(dto, staffProfileId, submissionId);

            boolean isJudge = hackathonRepository.existsJudge(hackathonId, staffProfileId);
            if (!isJudge) {
                throw new DomainException("Operazione non autorizzata: l'utente non è il giudice di questo hackathon");
            }

            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.IN_EVALUATION) {
                throw new DomainException("L'hackathon non è attualmente in fase di valutazione");
            }

            Submission submission = submissionRepository.getByIdAndHackathonId(submissionId, hackathonId);
            if (submission == null) {
                throw new DomainException("Sottomissione non trovata per questo hackathon");
            }

            boolean hasEvaluation = submission.hasEvaluation();
            if (hasEvaluation) {
                throw new DomainException("La sottomissione è già stata valutata");
            }

            submission.addEvaluation(dto.getScore(), dto.getComment());

            submissionRepository.save(submission);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}