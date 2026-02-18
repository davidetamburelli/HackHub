package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.StaffProfile;
import model.dto.CreateHackathonDTO;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;
import validators.HackathonValidator;

import java.util.ArrayList;
import java.util.List;

public class HackathonHandler {

    private final EntityManager em;

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;

    private final HackathonValidator hackathonValidator;

    public HackathonHandler(EntityManager em) {
        this.em = em;
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.userRepository = new UserRepository(em);
        this.teamRepository = new TeamRepository(em);
        this.hackathonValidator = new HackathonValidator(hackathonRepository, staffProfileRepository);
    }

    public Hackathon createHackathon(Long staffProfileId, CreateHackathonDTO dto) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            hackathonValidator.validate(dto, staffProfileId);

            StaffProfile organizer = staffProfileRepository.getById(staffProfileId);

            StaffProfile judge = staffProfileRepository.findByEmail(dto.getJudgeEmail());
            if (judge == null) {
                throw new DomainException("Giudice non trovato per email: " + dto.getJudgeEmail());
            }

            List<StaffProfile> mentors = new ArrayList<>();
            if (dto.getMentorEmails() != null) {
                for (String email : dto.getMentorEmails()) {
                    StaffProfile mentor = staffProfileRepository.findByEmail(email);
                    if (mentor != null) {
                        mentors.add(mentor);
                    }
                }
            }

            Hackathon hackathon = new Hackathon(
                    dto.getName(),
                    dto.getType(),
                    dto.getPrize(),
                    dto.getMaxTeamSize(),
                    dto.getRegulation(),
                    organizer,
                    judge,
                    mentors,
                    dto.getDelivery(),
                    dto.getLocation(),
                    dto.getRankingPolicy(),
                    dto.getSubscriptionDates(),
                    dto.getDates()
            );

            hackathonRepository.save(hackathon);

            tx.commit();
            return hackathon;

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}