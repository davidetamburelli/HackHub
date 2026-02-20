package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.Submission;
import model.dto.requestdto.AddSubmissionDTO;
import model.enums.HackathonStatus;
import repository.*;
import utils.DomainException;
import validators.SubmissionValidator;

import java.util.List;

public class SubmissionHandler {

    private final EntityManager em;
    private final SubmissionValidator submissionValidator;
    private final StaffProfileRepository staffProfileRepository;
    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public SubmissionHandler(EntityManager em) {
        this.em = em;
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.submissionRepository = new SubmissionRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
        this.submissionValidator = new SubmissionValidator();
    }

    public void createSubmission(Long userId, Long hackathonId, AddSubmissionDTO addSubmissionDTO) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            submissionValidator.validate(addSubmissionDTO);

            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING) {
                throw new DomainException("L'hackathon non è in corso");
            }

            ParticipatingTeam participatingTeam = participatingTeamRepository.findByHackathonIdAndActiveMemberId(hackathonId, userId);
            if (participatingTeam == null) {
                throw new DomainException("Utente non autorizzato o team non trovato");
            }

            boolean exists = submissionRepository.existsByParticipatingTeamId(participatingTeam.getId());
            if (exists) {
                throw new DomainException("Sottomissione già esistente per questo team");
            }

            boolean isDisqualified = participatingTeam.isDisqualified();
            if (isDisqualified) {
                throw new DomainException("Il team è stato squalificato");
            }

            Submission createdSubmission = new Submission(
                    hackathonId,
                    participatingTeam.getId(),
                    addSubmissionDTO.getResponse(),
                    addSubmissionDTO.getResponseURL()
            );

            submissionRepository.save(createdSubmission);

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