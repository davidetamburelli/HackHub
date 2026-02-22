package handlers;

import model.Invitation;
import model.Team;
import model.User;
import model.dto.responsedto.InvitationDetailsDTO;
import model.dto.responsedto.InvitationSummaryDTO;
import model.dto.responsedto.UserSummaryDTO;
import model.enums.InvitationStatus;
import model.mappers.InvitationDTOMapper;
import model.mappers.UserDTOMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.InvitationRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    public UserSummaryDTO searchUser(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new DomainException("Utente non trovato");
        }
        return UserDTOMapper.toSummary(user);
    }

    @Transactional
    public void inviteUser(Long inviterUserId, Long inviteeUserId) {
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

    }

    public List<InvitationSummaryDTO> getInvitationsList(Long userId) {
        List<Invitation> invitations =
                invitationRepository.findByInviteeId(userId);

        Set<Long> teamIds = invitations.stream()
                .map(Invitation::getTeamId)
                .collect(Collectors.toSet());

        List<Team> teams = teamRepository.findAllById(teamIds);

        Map<Long, String> teamNames = teams.stream()
                .collect(Collectors.toMap(
                        Team::getId,
                        Team::getName
                ));

        return invitations.stream()
                .map(inv -> InvitationDTOMapper.toSummary(
                        inv,
                        teamNames.get(inv.getTeamId())
                ))
                .toList();
    }

    public InvitationDetailsDTO getInvitationDetails(Long userId, Long invitationId) {
        Invitation invitation =
                invitationRepository.getByIdAndInviteeId(invitationId, userId);

        if (invitation == null) {
            throw new DomainException(
                    "Invito non trovato o non autorizzato"
            );
        }

        Team team = teamRepository.getById(invitation.getTeamId());

        return InvitationDTOMapper.toDetails(invitation, team);
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