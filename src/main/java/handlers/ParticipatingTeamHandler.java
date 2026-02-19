package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.Team;
import model.User;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import repository.UserRepository;
import validators.RegisterTeamToHackathonValidator;

public class ParticipatingTeamHandler {

    private final EntityManager em;

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    private final RegisterTeamToHackathonValidator validator;

    public ParticipatingTeamHandler(EntityManager em) {
        this.em = em;
        this.userRepository = new UserRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);

        this.validator = new RegisterTeamToHackathonValidator(
                hackathonRepository,
                teamRepository,
                userRepository,
                participatingTeamRepository
        );
    }

    public void registerTeamToHackathon(Long userId, Long hackathonId) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            validator.validate(userId, hackathonId);

            User user = userRepository.getById(userId);
            Team team = teamRepository.findByMemberId(user.getId());
            Hackathon hackathon = hackathonRepository.getById(hackathonId);

            ParticipatingTeam pt = new ParticipatingTeam(hackathon, team);

            hackathon.addParticipatingTeam(pt);

            participatingTeamRepository.save(pt);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}