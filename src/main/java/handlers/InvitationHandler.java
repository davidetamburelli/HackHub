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
        return userRepository.findByUsername(username);
    }

    public void inviteUser(Long inviterUserId, Long inviteeUserId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            User inviter = userRepository.getById(inviterUserId);
            User invitee = userRepository.getById(inviteeUserId);

            if (inviter == null) throw new DomainException("Utente invitante non trovato");
            if (invitee == null) throw new DomainException("Utente da invitare non trovato");

            Team team = teamRepository.findByMemberId(inviter.getId());
            if (team == null) throw new DomainException("Devi appartenere a un team per invitare qualcuno.");

            team.assertLeader(inviter);

            if (invitee.getTeamId() != null) {
                throw new DomainException("L'utente " + invitee.getUsername().getValue() + " fa già parte di un team.");
            }

            if (invitationRepository.existsPendingByTeamIdAndInviteeId(team.getId(), invitee.getId())) {
                throw new DomainException("Esiste già un invito in attesa per questo utente.");
            }

            Invitation invitation = new Invitation(team, invitee);
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

            invitation.assertInvitee(user);
            invitation.assertPending();

            invitation.accept();

            Team team = invitation.getTeam();
            team.addMember(user);

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

            invitation.assertInvitee(user);
            invitation.assertPending();

            invitation.reject();
            invitationRepository.save(invitation);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}