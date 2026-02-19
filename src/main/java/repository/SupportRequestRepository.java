package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import model.SupportRequest;

import java.util.List;

public class SupportRequestRepository extends AbstractRepository<SupportRequest> {

    public SupportRequestRepository(EntityManager em) {
        super(em, SupportRequest.class);
    }

    public List<SupportRequest> getByHackathonId(Long hackathonId) {
        String jpql = "SELECT s FROM SupportRequest s WHERE s.hackathon.id = :hackathonId";

        TypedQuery<SupportRequest> query = em.createQuery(jpql, SupportRequest.class);
        query.setParameter("hackathonId", hackathonId);

        return query.getResultList();
    }
}