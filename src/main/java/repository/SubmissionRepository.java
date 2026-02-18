package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Submission;

public class SubmissionRepository extends AbstractRepository<Submission> {

    public SubmissionRepository(EntityManager em) {
        super(em, Submission.class);
    }

    public Submission findByParticipatingTeamId(Long participatingTeamId) {
        try {
            String jpql = "SELECT s FROM Submission s WHERE s.participatingTeam.id = :ptId";

            TypedQuery<Submission> query = em.createQuery(jpql, Submission.class);
            query.setParameter("ptId", participatingTeamId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existsByParticipatingTeamId(Long participatingTeamId) {
        String jpql = "SELECT COUNT(s) FROM Submission s WHERE s.participatingTeam.id = :ptId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("ptId", participatingTeamId)
                .getSingleResult();

        return count > 0;
    }
}