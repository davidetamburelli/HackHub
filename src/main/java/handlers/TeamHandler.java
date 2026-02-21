package handlers;

import model.Team;
import model.User;
import model.dto.requestdto.CreateTeamDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

@Service
public class TeamHandler {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamHandler(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public void createTeam(Long userId, CreateTeamDTO createTeamDTO) {
        boolean existByName = teamRepository.existsByName(createTeamDTO.getName());

        if (existByName) {
            throw new DomainException("Esiste già un team con questo nome");
        }

        boolean userHasTeam = teamRepository.existsByMemberId(userId);

        if (userHasTeam) {
            throw new DomainException("L'utente fa già parte di un team");
        }

        User user = userRepository.getById(userId);

        if (user == null) {
            throw new DomainException("Utente non trovato");
        }

        Team createdTeam = new Team(createTeamDTO.getName(), userId);

        teamRepository.save(createdTeam);

        user.assignTeam(createdTeam.getId());

        userRepository.save(user);
    }
}