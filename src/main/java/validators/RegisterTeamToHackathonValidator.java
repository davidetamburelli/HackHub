package validators;

import model.Hackathon;
import model.Team;
import model.User;
import model.dto.RegisterTeamToHackathonDTO;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class RegisterTeamToHackathonValidator {

    private final HackathonRepository hackathonRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public RegisterTeamToHackathonValidator(
            HackathonRepository hackathonRepository,
            TeamRepository teamRepository,
            UserRepository userRepository,
            ParticipatingTeamRepository participatingTeamRepository) {
        this.hackathonRepository = hackathonRepository;
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.participatingTeamRepository = participatingTeamRepository;
    }

    public void validate(RegisterTeamToHackathonDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");
        if (dto.getUserId() == null || dto.getHackathonId() == null)
            throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");

        Hackathon hackathon = hackathonRepository.getById(dto.getHackathonId());
        if (hackathon == null) {
            throw new DomainException("Hackathon non trovato");
        }

        User user = userRepository.getById(dto.getUserId());
        if (user == null) {
            throw new DomainException("Utente non trovato");
        }

        hackathon.isInRegistration();

        Team team = teamRepository.findByMemberId(user.getId());
        if (team == null) {
            throw new DomainException("L'utente non appartiene a nessun team e non può iscriversi.");
        }

        team.assertLeader(user);

        if (participatingTeamRepository.existsByHackathonIdAndTeamId(hackathon.getId(), team.getId())) {
            throw new DomainException("Il team '" + team.getName() + "' è già iscritto a questo hackathon.");
        }

        hackathon.assertTeamSizeAllowed(team.getTeamSize());
    }
}