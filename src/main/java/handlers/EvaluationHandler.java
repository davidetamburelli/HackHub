package handlers;

import model.Hackathon;
import model.Submission;
import model.dto.requestdto.AddEvaluationDTO;
import model.enums.HackathonStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.SubmissionRepository;
import utils.DomainException;

@Service
public class EvaluationHandler {

    private final HackathonRepository hackathonRepository;
    private final SubmissionRepository submissionRepository;

    public EvaluationHandler(HackathonRepository hackathonRepository, SubmissionRepository submissionRepository) {
        this.hackathonRepository = hackathonRepository;
        this.submissionRepository = submissionRepository;
    }

    @Transactional
    public void addEvaluation(Long staffProfileId, Long hackathonId, Long submissionId, AddEvaluationDTO dto) {

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
    }

    @Transactional
    public void confirmEvaluations(Long staffProfileId, Long hackathonId) {

        boolean isJudge = hackathonRepository.existsJudge(hackathonId, staffProfileId);
        if (!isJudge) {
            throw new DomainException("Operazione non autorizzata: solo il giudice può confermare le valutazioni.");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.IN_EVALUATION) {
            throw new DomainException("Impossibile confermare le valutazioni: l'hackathon non è attualmente in fase di valutazione.");
        }

        boolean evaluationMissing = submissionRepository.existsByHackathonIdAndEvaluationIsNull(hackathonId);
        if (evaluationMissing) {
            throw new DomainException("Impossibile confermare: ci sono ancora sottomissioni senza valutazione per questo hackathon.");
        }

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) {
            throw new DomainException("Hackathon non trovato.");
        }

        hackathon.close();

        hackathonRepository.save(hackathon);
    }
}