package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Submission;
import model.dto.AddEvaluationDTO;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import repository.SubmissionRepository;
import validators.EvaluationValidator;

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

        this.evaluationValidator = new EvaluationValidator(
                hackathonRepository,
                staffProfileRepository,
                submissionRepository
        );
    }

    public void addEvaluation(Long staffProfileId, Long hackathonId, Long submissionId, AddEvaluationDTO dto) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            evaluationValidator.validate(dto, staffProfileId, submissionId);

            Submission submission = submissionRepository.getById(submissionId);

            submission.addEvaluation(dto.getScore(), dto.getComment());

            submissionRepository.save(submission);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}