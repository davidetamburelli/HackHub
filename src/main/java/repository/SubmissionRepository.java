package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.RankingCandidate;
import model.Submission;
import java.util.List;

public class SubmissionRepository extends AbstractRepository<Submission> {

    public SubmissionRepository(EntityManager em) {
        super(em, Submission.class);
    }

    public Submission findByParticipatingTeamId(Long participatingTeamId) {
        try {
            String jpql = "SELECT s FROM Submission s WHERE s.participatingTeam = :ptId";

            TypedQuery<Submission> query = em.createQuery(jpql, Submission.class);
            query.setParameter("ptId", participatingTeamId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existsByParticipatingTeamId(Long participatingTeamId) {
        String jpql = "SELECT COUNT(s) FROM Submission s WHERE s.participatingTeam = :ptId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("ptId", participatingTeamId)
                .getSingleResult();

        return count > 0;
    }

    public List<Submission> findByHackathonId(Long hackathonId) {
        String jpql = "SELECT s FROM Submission s WHERE s.hackathon = :hackathonId";

        TypedQuery<Submission> query = em.createQuery(jpql, Submission.class);
        query.setParameter("hackathonId", hackathonId);

        return query.getResultList();
    }

    public Submission getByIdAndHackathonId(Long submissionId, Long hackathonId) {
        try {
            String jpql = "SELECT s FROM Submission s " +
                    "WHERE s.id = :submissionId AND s.hackathon = :hackathonId";

            TypedQuery<Submission> query = em.createQuery(jpql, Submission.class);
            query.setParameter("submissionId", submissionId);
            query.setParameter("hackathonId", hackathonId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existsByHackathonIdAndEvaluationIsNull(Long hackathonId) {
        String jpql = "SELECT COUNT(s) FROM Submission s WHERE s.hackathon = :hackathonId AND s.evaluation IS NULL";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .getSingleResult();

        return count > 0;
    }

    public Submission findByHackathonIdAndParticipatingTeamId(Long hackathonId, Long participatingTeamId) {
        try {
            String jpql = "SELECT s FROM Submission s " +
                    "WHERE s.hackathon = :hackathonId AND s.participatingTeam = :ptId";

            TypedQuery<Submission> query = em.createQuery(jpql, Submission.class);
            query.setParameter("hackathonId", hackathonId);
            query.setParameter("ptId", participatingTeamId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<RankingCandidate> getRankingCandidates(Long hackathonId) {
        String jpql = "SELECT rc FROM RankingCandidate rc " +
                "JOIN ParticipatingTeam pt ON rc.eligibleParticipatingTeam = pt.id " +
                "WHERE pt.hackathon = :hackathonId AND pt.disqualified = false";

        return em.createQuery(jpql, RankingCandidate.class)
                .setParameter("hackathonId", hackathonId)
                .getResultList();
    }

}