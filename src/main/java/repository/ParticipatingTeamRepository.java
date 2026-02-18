package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.ParticipatingTeam;

public class ParticipatingTeamRepository extends AbstractRepository<ParticipatingTeam> {

    public ParticipatingTeamRepository(EntityManager em) {

        super(em, ParticipatingTeam.class);

    }

    public boolean existsByHackathonIdAndTeamId(Long hackathonId, Long teamId) {
        String jpql = "SELECT COUNT(pt) FROM ParticipatingTeam pt " +
                "WHERE pt.hackathon.id = :hackathonId AND pt.team.id = :teamId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("teamId", teamId)
                .getSingleResult();

        return count > 0;
    }

    public ParticipatingTeam findByHackathonIdAndTeamId(Long hackathonId, Long teamId) {
        try {
            String jpql = "SELECT pt FROM ParticipatingTeam pt " +
                    "WHERE pt.hackathon.id = :hackathonId AND pt.team.id = :teamId";

            TypedQuery<ParticipatingTeam> query = em.createQuery(jpql, ParticipatingTeam.class);
            query.setParameter("hackathonId", hackathonId);
            query.setParameter("teamId", teamId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}