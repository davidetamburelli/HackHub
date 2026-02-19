package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Team;
import model.User;
import model.dto.CreateTeamDTO;
import repository.TeamRepository;
import repository.UserRepository;
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
        this.teamValidator = new TeamValidator(teamRepository, userRepository);
    }

    public void createTeam(Long userId, CreateTeamDTO dto) {

        teamValidator.validate(dto, userId);

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            User leader = userRepository.getById(userId);

            Team team = new Team(dto.getName(), leader);

            teamRepository.save(team);

            team.addMember(leader);

            userRepository.save(leader);

            tx.commit();
            System.out.println("Team creato con successo: " + team.getName());

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}