package validators;

import model.Hackathon;
import model.ParticipatingTeam;
import model.Team;
import model.User;
import model.dto.CreateSupportRequestDTO;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class SupportRequestValidator {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public SupportRequestValidator(
            UserRepository userRepository,
            TeamRepository teamRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
    }

    public void validate(CreateSupportRequestDTO dto, Long userId, Long hackathonId) {

        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (userId == null || hackathonId == null) throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");

        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della richiesta è obbligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("L'urgenza è obbligatoria");
        }

        User user = userRepository.getById(userId);
        if (user == null) throw new DomainException("Utente non trovato");

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        Team team = teamRepository.findByMemberId(user.getId());
        if (team == null) {
            throw new DomainException("L'utente non appartiene a nessun team.");
        }

        // Se solo il leader può aprire la support request
        // team.assertLeader(user);

        ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathon.getId(), team.getId());
        if (pt == null) {
            throw new DomainException("Il team non è iscritto a questo hackathon. Non è possibile aprire ticket.");
        }
    }
}