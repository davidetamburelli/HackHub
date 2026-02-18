package repository;

import jakarta.persistence.EntityManager;
import model.Hackathon;

public class HackathonRepository extends AbstractRepository<Hackathon> {

    public HackathonRepository(EntityManager em) {
        super(em, Hackathon.class);
    }

    public boolean existsByName(String name) {
        String jpql = "SELECT COUNT(h) FROM Hackathon h WHERE h.name = :name";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count > 0;
    }
}