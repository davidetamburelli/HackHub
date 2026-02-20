package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Invitation;
import model.Team;
import model.User;
import repository.InvitationRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class InvitationHandler {

    private final EntityManager em;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;

    public InvitationHandler(EntityManager em) {
        this.em = em;
        this.userRepository = new UserRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.invitationRepository = new InvitationRepository(em);
    }

    public User searchUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new DomainException("Utente non trovato");
        }
        return user;
    }

    public void inviteUser(Long inviterUserId, Long inviteeUserId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Team inviterTeam = teamRepository.findByLeaderId(inviterUserId);
            if (inviterTeam == null) {
                throw new DomainException("Operazione negata: non sei il leader di nessun team.");
            }

            Team inviteeTeam = teamRepository.findByMemberId(inviteeUserId);
            if (inviteeTeam != null) {
                throw new DomainException("Impossibile invitare: l'utente fa già parte di un team.");
            }

            boolean existingInvitation = invitationRepository.existsPendingByTeamIdAndInviteeId(inviterTeam.getId(), inviteeUserId);
            if (existingInvitation) {
                throw new DomainException("Esiste già un invito in attesa per questo utente.");
            }

            Invitation invitation = new Invitation(inviterTeam.getId(), inviteeUserId);

            invitationRepository.save(invitation);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void acceptInvitation(Long userId, Long invitationId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            User user = userRepository.getById(userId);
            Invitation invitation = invitationRepository.getById(invitationId);

            if (user == null || invitation == null) throw new DomainException("Invito o Utente non trovati");

            if (!invitation.getInvitee().equals(userId)) {
                throw new DomainException("Operazione non autorizzata: l'invito non è rivolto a te.");
            }

            invitation.accept();

            Team team = teamRepository.getById(invitation.getTeamId());

            team.addMember(userId);
            user.assignTeam(team.getId());

            invitationRepository.save(invitation);
            teamRepository.save(team);
            userRepository.save(user);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void rejectInvitation(Long userId, Long invitationId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            User user = userRepository.getById(userId);
            Invitation invitation = invitationRepository.getById(invitationId);

            if (user == null || invitation == null) throw new DomainException("Invito o Utente non trovati");

            if (!invitation.getInvitee().equals(userId)) {
                throw new DomainException("Operazione non autorizzata: l'invito non è rivolto a te.");
            }

            invitation.reject();

            invitationRepository.save(invitation);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}