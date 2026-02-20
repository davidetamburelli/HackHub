package handlers;

import builders.HackathonBuilder;
import builders.IHackathonBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.StaffProfile;
import model.dto.CreateHackathonDTO;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import utils.DomainException;
import validators.HackathonValidator;

import java.util.ArrayList;
import java.util.List;

public class HackathonHandler {

    private final EntityManager em;

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;

    private final HackathonValidator hackathonValidator;

    public HackathonHandler(EntityManager em) {
        this.em = em;
        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.hackathonValidator = new HackathonValidator();

    }

    public void createHackathon(Long staffProfileId, CreateHackathonDTO dto) {
        if (staffProfileId == null) throw new IllegalArgumentException("L'ID dell'organizzatore è obbligatorio");

        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            hackathonValidator.validate(dto);

            StaffProfile organizer = staffProfileRepository.getById(staffProfileId);
            if (organizer == null) {
                throw new DomainException("Organizzatore non trovato (ID: " + staffProfileId + ")");
            }

            if (hackathonRepository.existsByName(dto.getName())) {
                throw new DomainException("Esiste già un hackathon con questo nome: " + dto.getName());
            }

            StaffProfile judge = staffProfileRepository.findByEmail(dto.getJudgeEmail());
            if (judge == null) {
                throw new DomainException("Nessun profilo staff trovato per l'email del giudice: " + dto.getJudgeEmail());
            }

            List<Long> mentorIds = new ArrayList<>();
            if (dto.getMentorEmails() != null && !dto.getMentorEmails().isEmpty()) {
                for (String email : dto.getMentorEmails()) {
                    StaffProfile mentor = staffProfileRepository.findByEmail(email);
                    if (mentor == null) {
                        throw new DomainException("Nessun profilo staff trovato per il mentore: " + email);
                    }
                    mentorIds.add(mentor.getId());
                }
            }

            IHackathonBuilder builder = new HackathonBuilder();

            Hackathon hackathon = builder
                    .buildName(dto.getName())
                    .buildType(dto.getType())
                    .buildPrize(dto.getPrize())
                    .buildMaxTeamSize(dto.getMaxTeamSize())
                    .buildRegulation(dto.getRegulation())
                    .buildDelivery(dto.getDelivery())
                    .buildLocation(dto.getLocation())
                    .buildRankingPolicy(dto.getRankingPolicy())
                    .buildSubscriptionDates(dto.getSubscriptionDates())
                    .buildDates(dto.getDates())
                    .buildOrganizer(organizer.getId())
                    .buildJudge(judge.getId())
                    .buildMentors(mentorIds)
                    .build();

            hackathonRepository.save(hackathon);

            tx.commit();
            System.out.println("Hackathon creato con successo: " + hackathon.getName());

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}