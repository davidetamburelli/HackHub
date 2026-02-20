package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Team;
import model.User;
import model.dto.CreateTeamDTO;
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

    public void createTeam(Long userId, CreateTeamDTO dto) {
        if (userId == null) throw new IllegalArgumentException("L'ID utente è obbligatorio");

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            teamValidator.validate(dto);

            if (teamRepository.existsByName(dto.getName())) {
                throw new DomainException("Esiste già un team con il nome: " + dto.getName());
            }

            if (teamRepository.existsByMemberId(userId)) {
                throw new DomainException("L'utente è già membro di un team e non può crearne uno nuovo.");
            }

            User leader = userRepository.getById(userId);
            if (leader == null) {
                throw new DomainException("Utente leader non trovato con ID: " + userId);
            }

            Team team = new Team(dto.getName(), userId);

            teamRepository.save(team);

            leader.assignTeam(team.getId());

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