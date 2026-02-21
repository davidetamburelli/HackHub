package handlers;

import model.Invitation;
import model.Team;
import model.User;
import model.enums.InvitationStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.InvitationRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

import java.util.List;

@Service
public class InvitationHandler {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final InvitationRepository invitationRepository;

    public InvitationHandler(
            UserRepository userRepository,
            TeamRepository teamRepository,
            InvitationRepository invitationRepository) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.invitationRepository = invitationRepository;
    }

    public User searchUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new DomainException("Utente non trovato");
        }
        return user;
    }

    @Transactional
    public Invitation inviteUser(Long inviterUserId, Long inviteeUserId) {
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

        return createdInvitation;
    }

    public List<Invitation> getInvitationsList(Long userId) {
        return invitationRepository.findByInviteeId(userId);
    }

    public Invitation getInvitationDetails(Long userId, Long invitationId) {
        return invitationRepository.getByIdAndInviteeId(invitationId, userId);
    }

    @Transactional
    public void acceptInvitation(Long userId, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);

        if (invitation == null) {
            throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
        }

        boolean userHasTeam = teamRepository.existsByMemberId(userId);
        if (userHasTeam) {
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
    }

    @Transactional
    public void rejectInvitation(Long userId, Long invitationId) {
        Invitation invitation = invitationRepository.findByIdAndInviteeIdAndStatus(invitationId, userId, InvitationStatus.PENDING);

        if (invitation == null) {
            throw new DomainException("Non è stato trovato l'invito selezionato per questo utente");
        }

        invitation.reject();
        invitationRepository.save(invitation);
    }
}