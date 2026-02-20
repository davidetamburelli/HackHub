package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.*;
import model.Hackathon;
import model.dto.HackathonSearchCriteria;
import model.enums.HackathonStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public List<Hackathon> search(HackathonSearchCriteria c) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Hackathon> cq = cb.createQuery(Hackathon.class);
        Root<Hackathon> h = cq.from(Hackathon.class);

        List<Predicate> preds = new ArrayList<>();

        if (c.nameContains() != null && !c.nameContains().isBlank()) {
            String like = "%" + c.nameContains().toLowerCase() + "%";
            preds.add(cb.like(cb.lower(h.get("name")), like));
        }

        if (c.status() != null) {
            preds.add(cb.equal(h.get("status"), c.status()));
        }

        if (c.isOnline() != null) {
            if (c.isOnline()) {
                preds.add(cb.isNull(h.get("location")));
            } else {
                preds.add(cb.isNotNull(h.get("location")));
            }
        }

        Path<LocalDate> startDate = h.get("dates").get("startDate");

        if (c.startsAfter() != null) {
            preds.add(cb.greaterThanOrEqualTo(startDate, c.startsAfter()));
        }

        if (c.startsBefore() != null) {
            preds.add(cb.lessThanOrEqualTo(startDate, c.startsBefore()));
        }

        if (!preds.isEmpty()) {
            cq.where(cb.and(preds.toArray(new Predicate[0])));
        }

        cq.orderBy(cb.asc(startDate));

        return em.createQuery(cq).getResultList();
    }

    public HackathonStatus findStatusByHackathonId(Long hackathonId) {
        try {
            String jpql = "SELECT h.status FROM Hackathon h WHERE h.id = :id";
            return em.createQuery(jpql, HackathonStatus.class)
                    .setParameter("id", hackathonId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean existsOrganizer(Long hackathonId, Long staffProfileId) {
        String jpql = "SELECT COUNT(h) FROM Hackathon h " +
                "WHERE h.id = :hackathonId AND h.organizer = :staffId";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("staffId", staffProfileId)
                .getSingleResult();

        return count > 0;
    }

    public boolean existsMentor(Long hackathonId, Long staffProfileId) {
        String jpql = "SELECT COUNT(h) FROM Hackathon h " +
                "WHERE h.id = :hackathonId AND :staffId MEMBER OF h.mentors";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("staffId", staffProfileId)
                .getSingleResult();
        return count > 0;
    }

    public boolean existsJudge(Long hackathonId, Long staffProfileId) {
        String jpql = "SELECT COUNT(h) FROM Hackathon h " +
                "WHERE h.id = :hackathonId AND h.judge = :staffId";
        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("staffId", staffProfileId)
                .getSingleResult();
        return count > 0;
    }

    public int findMaxTeamSizeByHackathonId(Long hackathonId) {
        try {
            String jpql = "SELECT h.maxTeamSize FROM Hackathon h WHERE h.id = :id";
            Integer size = em.createQuery(jpql, Integer.class)
                    .setParameter("id", hackathonId)
                    .getSingleResult();
            return size != null ? size : 0;
        } catch (NoResultException e) {
            return 0;
        }
    }

    public boolean existsStaff(Long hackathonId, Long staffProfileId) {
        String jpql = "SELECT COUNT(h) FROM Hackathon h " +
                "WHERE h.id = :hackathonId " +
                "AND (h.organizer = :staffId OR h.judge = :staffId OR :staffId MEMBER OF h.mentors)";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("hackathonId", hackathonId)
                .setParameter("staffId", staffProfileId)
                .getSingleResult();

        return count > 0;
    }

    public List<Hackathon> findAll() {
        String jpql = "SELECT h FROM Hackathon h";
        return em.createQuery(jpql, Hackathon.class).getResultList();
    }
}