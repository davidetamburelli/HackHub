package validators;

import model.Hackathon;
import model.Team;
import model.User;
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

    public void validate(Long userId, Long hackathonId) {
        if (userId == null || hackathonId == null)
            throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        User user = userRepository.getById(userId);
        if (user == null) throw new DomainException("Utente non trovato");

        hackathon.assertInRegistration();

        Team team = teamRepository.findByMemberId(user.getId());
        if (team == null) throw new DomainException("L'utente non ha un team");

        team.assertLeader(user);

        if (participatingTeamRepository.existsByHackathonIdAndTeamId(hackathon.getId(), team.getId())) {
            throw new DomainException("Team già iscritto a questo hackathon");
        }

        hackathon.assertTeamSizeAllowed(team.getTeamSize());
    }
}