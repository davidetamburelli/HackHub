package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Team;
import org.springframework.stereotype.Repository;

@Repository
public class TeamRepository extends AbstractRepository<Team> {

    public TeamRepository(EntityManager em) {
        super(em, Team.class);
    }

    public boolean existsByName(String name) {
        String jpql = "SELECT COUNT(t) FROM Team t WHERE t.name = :name";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsByMemberId(Long userId) {
        String jpql = "SELECT COUNT(t) FROM Team t WHERE :userId MEMBER OF t.members";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count > 0;
    }

    public Team findByMemberId(Long userId) {
        try {
            String jpql = "SELECT t FROM Team t WHERE :userId MEMBER OF t.members";
            TypedQuery<Team> query = em.createQuery(jpql, Team.class);
            query.setParameter("userId", userId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Team findByLeaderId(Long userId) {
        try {
            String jpql = "SELECT t FROM Team t WHERE t.leader = :userId";
            TypedQuery<Team> query = em.createQuery(jpql, Team.class);
            query.setParameter("userId", userId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}