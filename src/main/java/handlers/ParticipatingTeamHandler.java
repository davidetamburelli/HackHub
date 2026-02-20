package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.ParticipatingTeam;
import model.Team;
import model.dto.RegisterTeamDTO;
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

    public void registerTeamToHackathon(Long userId, Long hackathonId, RegisterTeamDTO dto) {

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            validator.validate(dto);

            Team team = teamRepository.findByLeaderId(userId);
            if (team == null) {
                throw new DomainException("Operazione non autorizzata: non sei il leader di nessun team.");
            }

            HackathonStatus hackathonStatus = hackathonRepository.findStatusById(hackathonId);
            if (hackathonStatus != HackathonStatus.IN_REGISTRATION) {
                throw new DomainException("L'hackathon non è attualmente aperto alle iscrizioni");
            }

            boolean isAlreadyRegistered = participatingTeamRepository.existsByHackathonIdAndTeamId(hackathonId, team.getId());
            if (isAlreadyRegistered) {
                throw new DomainException("Team già iscritto a questo hackathon");
            }

            int maxTeamSize = hackathonRepository.findMaxTeamSizeById(hackathonId);

            int teamSize = team.getTeamSize();
            if (teamSize > maxTeamSize || teamSize < 1) {
                throw new DomainException("Il numero dei membri del team (" + teamSize + ") non è valido o supera il limite massimo consentito (" + maxTeamSize + ")");
            }

            List<Long> memberIdsSnapshot = team.getMemberIdsSnapshot();

            ParticipatingTeam pt = new ParticipatingTeam(
                    hackathonId,
                    team.getId(),
                    memberIdsSnapshot,
                    dto.getContactEmail(),
                    dto.getPayoutMethod(),
                    dto.getPayoutRef(),
                    LocalDateTime.now()
            );

            participatingTeamRepository.save(pt);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}