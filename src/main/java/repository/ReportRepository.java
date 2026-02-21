package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Report;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ReportRepository extends AbstractRepository<Report> {

    public ReportRepository(EntityManager em) {
        super(em, Report.class);
    }

    public List<Report> getByHackathonId(Long hackathonId) {
        String jpql = "SELECT r FROM Report r WHERE r.hackathon = :hackathonId";

        TypedQuery<Report> query = em.createQuery(jpql, Report.class);
        query.setParameter("hackathonId", hackathonId);

        return query.getResultList();
    }

    public Report getByIdAndHackathonId(Long reportId, Long hackathonId) {
        try {
            String jpql = "SELECT r FROM Report r WHERE r.id = :reportId AND r.hackathon = :hackathonId";

            TypedQuery<Report> query = em.createQuery(jpql, Report.class);
            query.setParameter("reportId", reportId);
            query.setParameter("hackathonId", hackathonId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}