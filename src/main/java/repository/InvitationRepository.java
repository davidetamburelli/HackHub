package repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import model.Invitation;
import model.enums.InvitationStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvitationRepository extends AbstractRepository<Invitation> {

    public InvitationRepository(EntityManager em) {
        super(em, Invitation.class);
    }

    public boolean existsPendingByTeamIdAndInviteeId(Long teamId, Long inviteeId) {
        String jpql = "SELECT COUNT(i) FROM Invitation i " +
                "WHERE i.team = :teamId " +
                "AND i.invitee = :inviteeId " +
                "AND i.status = :status";

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("teamId", teamId)
                .setParameter("inviteeId", inviteeId)
                .setParameter("status", InvitationStatus.PENDING)
                .getSingleResult();

        return count > 0;
    }

    public Invitation findByIdAndInviteeIdAndStatus(Long invitationId, Long userId, InvitationStatus status) {
        try {
            String jpql = "SELECT i FROM Invitation i " +
                    "WHERE i.id = :invitationId " +
                    "AND i.invitee = :userId " +
                    "AND i.status = :status";

            TypedQuery<Invitation> query = em.createQuery(jpql, Invitation.class);
            query.setParameter("invitationId", invitationId);
            query.setParameter("userId", userId);
            query.setParameter("status", status);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
    
    public List<Invitation> findByInviteeId(Long userId) {
        String jpql = "SELECT i FROM Invitation i WHERE i.invitee = :userId";

        TypedQuery<Invitation> query = em.createQuery(jpql, Invitation.class);
        query.setParameter("userId", userId);

        return query.getResultList();
    }

    public Invitation getByIdAndInviteeId(Long invitationId, Long userId) {
        try {
            String jpql = "SELECT i FROM Invitation i " +
                    "WHERE i.id = :invitationId " +
                    "AND i.invitee = :userId";

            TypedQuery<Invitation> query = em.createQuery(jpql, Invitation.class);
            query.setParameter("invitationId", invitationId);
            query.setParameter("userId", userId);

            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}