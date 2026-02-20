package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.User;

public class UserRepository extends AbstractRepository<User> {

    public UserRepository(EntityManager em) {
        super(em, User.class);
    }

    public User findByUsername(String username) {
        try {
            String jpql = "SELECT u FROM User u WHERE u.username = :username";

            TypedQuery<User> query = em.createQuery(jpql, User.class);
            query.setParameter("username", username);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}