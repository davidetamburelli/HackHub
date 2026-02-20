package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.ParticipatingTeam;

import java.util.List;

public class ParticipatingTeamRepository extends AbstractRepository<ParticipatingTeam> {

    public ParticipatingTeamRepository(EntityManager em) {
        super(em, ParticipatingTeam.class);
    }

    public boolean existsByHackathonIdAndTeamId(Long hackathonId, Long teamId) {
        String jpql = "SELECT COUNT(pt) FROM ParticipatingTeam pt " +
                "WHERE pt.hackathon = :hackathonId AND pt.team = :teamId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("teamId", teamId)
                .getSingleResult();

        return count > 0;
    }

    public ParticipatingTeam findByHackathonIdAndTeamId(Long hackathonId, Long teamId) {
        try {
            String jpql = "SELECT pt FROM ParticipatingTeam pt " +
                    "WHERE pt.hackathon = :hackathonId AND pt.team = :teamId";

            TypedQuery<ParticipatingTeam> query = em.createQuery(jpql, ParticipatingTeam.class);
            query.setParameter("hackathonId", hackathonId);
            query.setParameter("teamId", teamId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ParticipatingTeam getByIdAndHackathonId(Long participatingTeamId, Long hackathonId) {
        try {
            String jpql = "SELECT pt FROM ParticipatingTeam pt " +
                    "WHERE pt.id = :ptId AND pt.hackathon = :hackathonId";

            TypedQuery<ParticipatingTeam> query = em.createQuery(jpql, ParticipatingTeam.class);
            query.setParameter("ptId", participatingTeamId);
            query.setParameter("hackathonId", hackathonId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public ParticipatingTeam findByHackathonIdAndActiveMemberId(Long hackathonId, Long userId) {
        try {
            String jpql = "SELECT pt FROM ParticipatingTeam pt " +
                    "WHERE pt.hackathon = :hackathonId AND :userId MEMBER OF pt.activeMembers";

            TypedQuery<ParticipatingTeam> query = em.createQuery(jpql, ParticipatingTeam.class);
            query.setParameter("hackathonId", hackathonId);
            query.setParameter("userId", userId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<ParticipatingTeam> findEligibleForRanking(Long hackathonId) {
        String jpql = "SELECT pt FROM ParticipatingTeam pt " +
                "WHERE pt.hackathon = :hackathonId " +
                "AND pt.disqualified = false";

        TypedQuery<ParticipatingTeam> query = em.createQuery(jpql, ParticipatingTeam.class);
        query.setParameter("hackathonId", hackathonId);

        return query.getResultList();
    }
}