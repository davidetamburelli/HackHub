package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Team;
import model.User;
import model.dto.requestdto.CreateTeamDTO;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;
import validators.TeamValidator;

public class TeamHandler {

    private final EntityManager em;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamValidator teamValidator;

    public TeamHandler(EntityManager em) {
        this.em = em;
        this.teamRepository = new TeamRepository(em);
        this.userRepository = new UserRepository(em);
        this.teamValidator = new TeamValidator();
    }

    public void createTeam(Long userId, CreateTeamDTO createTeamDTO) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            teamValidator.validate(createTeamDTO);

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

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}