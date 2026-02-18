package repository;

import jakarta.persistence.EntityManager;
import model.User;

public class UserRepository extends AbstractRepository<User> {

    public UserRepository(EntityManager em) {
        super(em, User.class);
    }

}
