package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.ParticipatingTeam;
import model.Team;
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

            Team team = teamRepository.findByMemberId(userId);

            ParticipatingTeam pt = new ParticipatingTeam(
                    hackathonId,
                    team.getId(),
                    team.getMemberIdsSnapshot()
            );

            participatingTeamRepository.save(pt);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}