package handlers;

import model.Hackathon;
import model.ParticipatingTeam;
import model.Submission;
import model.dto.requestdto.AddSubmissionDTO;
import model.dto.responsedto.SubmissionDetailsDTO;
import model.dto.responsedto.SubmissionSummaryDTO;
import model.enums.HackathonStatus;
import model.mappers.SubmissionDTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.StaffProfileRepository;
import repository.SubmissionRepository;
import utils.DomainException;

import java.util.List;

@Service
public class SubmissionHandler {

    private final StaffProfileRepository staffProfileRepository;
    private final SubmissionRepository submissionRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public SubmissionHandler(
            StaffProfileRepository staffProfileRepository,
            SubmissionRepository submissionRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository) {
        this.staffProfileRepository = staffProfileRepository;
        this.submissionRepository = submissionRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
    }

    @Transactional
    public void createSubmission(Long userId, Long hackathonId, AddSubmissionDTO addSubmissionDTO) {
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
    }

    public List<SubmissionSummaryDTO> getSubmissionsList(Long staffProfileId, Long hackathonId) {
        boolean isStaff = hackathonRepository.existsStaff(hackathonId, staffProfileId);
        if (!isStaff) {
            throw new DomainException("Operazione non autorizzata: non fai parte dello staff");
        }

        List<Submission> submissions =
                submissionRepository.findByHackathonId(hackathonId);

        return submissions.stream()
                .map(SubmissionDTOMapper::toSummary)
                .toList();
    }

    public SubmissionDetailsDTO getSubmissionDetails(Long staffProfileId, Long hackathonId, Long submissionId) {
        boolean isStaff = hackathonRepository.existsStaff(hackathonId, staffProfileId);
        if (!isStaff) {
            throw new DomainException("Operazione non autorizzata: non fai parte dello staff");
        }

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) {
            throw new DomainException("Hackathon non trovato");
        }

        Submission submission = submissionRepository.getByIdAndHackathonId(submissionId, hackathonId);
        if (submission == null) {
            throw new DomainException("La sottomissione non appartiene all'hackathon selezionato");
        }

        return SubmissionDTOMapper.toDetails(submission);
    }
}