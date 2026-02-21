package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Invitation;
import model.Team;
import model.User;
import model.enums.InvitationStatus;
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

    public Invitation inviteUser(Long inviterUserId, Long inviteeUserId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Team inviterTeam = teamRepository.findByLeaderId(inviterUserId);
            if (inviterTeam == null) {
                throw new DomainException("Operazione negata: non sei il leader di alcun team");
            }

            Team inviteeTeam = teamRepository.findByMemberId(inviteeUserId);
            if (inviteeTeam != null) {
                throw new DomainException("L'utente fa già parte di un team");
            }

            boolean existingInvitation = invitationRepository.existsPendingByTeamIdAndInviteeId(inviterTeam.getId(), inviteeUserId);
            if (existingInvitation) {
                throw new DomainException("Esiste già un invito in attesa di risposta per questo utente");
            }

            Invitation createdInvitation = new Invitation(inviterTeam.getId(), inviteeUserId);

            invitationRepository.save(createdInvitation);

            tx.commit();
            return createdInvitation;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void acceptInvitation(Long userId, Long invitationId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);

            if (invitation == null) {
                throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
            }

            boolean userHasTeam = teamRepository.existsByMemberId(userId);
            if(userHasTeam) {
                throw new DomainException("L'utente appartiene già a un team");
            }

            User user = userRepository.getById(invitation.getInvitee());
            Team team = teamRepository.getById(invitation.getTeamId());
            team.addMember(userId);
            user.assignTeam(team.getId());
            invitation.accept();

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

            Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);

            if (invitation == null) {
                throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
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