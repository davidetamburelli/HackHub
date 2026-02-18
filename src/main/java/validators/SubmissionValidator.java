package validators;

import model.Hackathon;
import model.ParticipatingTeam;
import model.Team;
import model.User;
import model.dto.AddSubmissionDTO;
import repository.*;
import utils.DomainException;

public class SubmissionValidator {

    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SubmissionRepository submissionRepository;

    public SubmissionValidator(
            HackathonRepository hackathonRepository,
            UserRepository userRepository,
            TeamRepository teamRepository,
            ParticipatingTeamRepository participatingTeamRepository,
            SubmissionRepository submissionRepository
    ) {
        this.hackathonRepository = hackathonRepository;
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.submissionRepository = submissionRepository;
    }

    public void validate(AddSubmissionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");

        if (dto.getUserId() == null || dto.getHackathonId() == null) {
            throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");
        }
        if (dto.getResponse() == null || dto.getResponse().isBlank()) {
            throw new IllegalArgumentException("Il testo della risposta è obbligatorio");
        }
        if (dto.getResponseURL() == null || dto.getResponseURL().isBlank()) {
            throw new IllegalArgumentException("L'URL del progetto (es. GitHub) è obbligatorio");
        }

        Hackathon hackathon = hackathonRepository.getById(dto.getHackathonId());
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        User user = userRepository.getById(dto.getUserId());
        if (user == null) throw new DomainException("Utente non trovato");

        hackathon.assertRunning();

        Team team = teamRepository.findByMemberId(user.getId());
        if (team == null) {
            throw new DomainException("L'utente non fa parte di alcun team.");
        }

        team.assertLeader(user);

        ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathon.getId(), team.getId());
        if (pt == null) {
            throw new DomainException("Il team non è iscritto a questo hackathon.");
        }

        if (submissionRepository.existsByParticipatingTeamId(pt.getId())) {
            throw new DomainException("Il team ha già inviato una soluzione per questo hackathon.");
        }
    }
}