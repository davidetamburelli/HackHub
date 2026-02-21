package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.StaffProfile;
import org.springframework.stereotype.Repository;

@Repository
public class StaffProfileRepository extends AbstractRepository<StaffProfile> {

    public StaffProfileRepository(EntityManager em) {
        super(em, StaffProfile.class);
    }

    public StaffProfile findByEmail(String emailString) {
        try {
            String jpql = "SELECT s FROM StaffProfile s WHERE s.email.value = :email";

            TypedQuery<StaffProfile> query = em.createQuery(jpql, StaffProfile.class);
            query.setParameter("email", emailString);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}