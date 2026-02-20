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

            if (!team.getLeader().equals(inviter.getId())) {
                throw new DomainException("Solo il leader può invitare nuovi membri.");
            }

            if (invitee.getTeam() != null) {
                throw new DomainException("L'utente " + invitee.getUsername() + " fa già parte di un team.");
            }

            if (invitationRepository.existsPendingByTeamIdAndInviteeId(team.getId(), invitee.getId())) {
                throw new DomainException("Esiste già un invito in attesa per questo utente.");
            }

            Invitation invitation = new Invitation(team.getId(), invitee.getId());
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