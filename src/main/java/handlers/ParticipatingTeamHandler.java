package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.ParticipatingTeam;
import model.Team;
import model.dto.requestdto.RegisterTeamDTO;
import model.enums.HackathonStatus;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import utils.DomainException;
import validators.ParticipatingTeamValidator;

import java.time.LocalDateTime;
import java.util.List;

public class ParticipatingTeamHandler {

    private final EntityManager em;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ParticipatingTeamValidator validator;

    public ParticipatingTeamHandler(EntityManager em) {
        this.em = em;
        this.teamRepository = new TeamRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
        this.validator = new ParticipatingTeamValidator();
    }

    public void registerTeamToHackathon(Long userId, Long hackathonId, RegisterTeamDTO registerTeamDTO) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            validator.validate(registerTeamDTO);

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

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}