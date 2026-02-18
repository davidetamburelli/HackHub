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

    public void validate(AddSubmissionDTO dto, Long userId, Long hackathonId) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");
        if (userId == null || hackathonId == null) throw new IllegalArgumentException("ID mancanti");

        if (dto.getResponse() == null || dto.getResponse().isBlank())
            throw new IllegalArgumentException("Testo risposta mancante");

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        hackathon.assertRunning();

        User user = userRepository.getById(userId);
        if (user == null) throw new DomainException("Utente non trovato");

        Team team = teamRepository.findByMemberId(user.getId());
        if (team == null) throw new DomainException("L'utente non ha un team");

        team.assertLeader(user);

        ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathon.getId(), team.getId());
        if (pt == null) throw new DomainException("Team non iscritto all'hackathon");

        if (submissionRepository.existsByParticipatingTeamId(pt.getId())) {
            throw new DomainException("Soluzione già inviata");
        }
    }
}