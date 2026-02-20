package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.Team;
import model.User;
import model.enums.HackathonStatus;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class ParticipatingTeamHandler {

    private final EntityManager em;

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    
    public ParticipatingTeamHandler(EntityManager em) {
        this.em = em;
        this.userRepository = new UserRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
    }

    public void registerTeamToHackathon(Long userId, Long hackathonId) {

        if (userId == null || hackathonId == null) {
            throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");
        }

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Hackathon hackathon = hackathonRepository.getById(hackathonId);
            if (hackathon == null) throw new DomainException("Hackathon non trovato");

            User user = userRepository.getById(userId);
            if (user == null) throw new DomainException("Utente non trovato");

            if (hackathon.getStatus() != HackathonStatus.IN_REGISTRATION) {
                throw new DomainException("L'hackathon non è attualmente aperto alle iscrizioni");
            }

            Team team = teamRepository.findByMemberId(userId);
            if (team == null) throw new DomainException("L'utente non ha un team");

            if (!team.getLeader().equals(userId)) {
                throw new DomainException("Solo il leader può iscrivere il team all'hackathon");
            }

            if (participatingTeamRepository.existsByHackathonIdAndTeamId(hackathon.getId(), team.getId())) {
                throw new DomainException("Team già iscritto a questo hackathon");
            }

            if (team.getMembers().size() > hackathon.getMaxTeamSize()) {
                throw new DomainException("Il numero dei membri del team supera il limite massimo consentito per questo hackathon (" + hackathon.getMaxTeamSize() + ")");
            }

            ParticipatingTeam pt = new ParticipatingTeam(
                    hackathonId,
                    team.getId(),
                    team.getMemberIdsSnapshot()
            );

            participatingTeamRepository.save(pt);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}