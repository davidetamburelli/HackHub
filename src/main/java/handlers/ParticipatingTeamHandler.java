package handlers;

import model.ParticipatingTeam;
import model.Team;
import model.dto.requestdto.RegisterTeamDTO;
import model.enums.HackathonStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import utils.DomainException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipatingTeamHandler {

    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public ParticipatingTeamHandler(
            TeamRepository teamRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository) {
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
    }

    @Transactional
    public void registerTeamToHackathon(Long userId, Long hackathonId, RegisterTeamDTO registerTeamDTO) {
        Team team = teamRepository.findByLeaderId(userId);
        if (team == null) {
            throw new DomainException("Utente non autorizzato: non sei il leader di alcun team");
        }

        HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
        if (hackathonStatus != HackathonStatus.IN_REGISTRATION) {
            throw new DomainException("Le iscrizioni per questo hackathon non sono aperte");
        }

        boolean isAlreadyRegistered = participatingTeamRepository.existsByHackathonIdAndTeamId(hackathonId, team.getId());
        if (isAlreadyRegistered) {
            throw new DomainException("Il team è già iscritto a questo hackathon");
        }

        int maxTeamSize = hackathonRepository.findMaxTeamSizeByHackathonId(hackathonId);
        int teamSize = team.getTeamSize();

        if (teamSize > maxTeamSize || teamSize < 1) {
            throw new DomainException("La dimensione del team non rispetta i limiti dell'hackathon");
        }

        List<Long> membersSnapshot = team.getMemberIdsSnapshot();

        ParticipatingTeam participatingTeam = new ParticipatingTeam(
                hackathonId,
                team.getId(),
                membersSnapshot,
                registerTeamDTO.getContactEmail(),
                registerTeamDTO.getPayoutMethod(),
                registerTeamDTO.getPayoutRef(),
                LocalDateTime.now()
        );

        participatingTeamRepository.save(participatingTeam);
    }
}