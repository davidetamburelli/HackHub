package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.Hackathon;
import model.ParticipatingTeam;
import model.Report;
import model.StaffProfile;
import model.dto.CreateReportDTO;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.ReportRepository;
import repository.StaffProfileRepository;
import validators.ReportValidator;

public class ReportHandler {

    private final EntityManager em;

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ReportRepository reportRepository;

    private final ReportValidator reportValidator;

    public ReportHandler(EntityManager em) {
        this.em = em;

        this.staffProfileRepository = new StaffProfileRepository(em);
        this.hackathonRepository = new HackathonRepository(em);
        this.participatingTeamRepository = new ParticipatingTeamRepository(em);
        this.reportRepository = new ReportRepository(em);

        this.reportValidator = new ReportValidator(
                staffProfileRepository,
                hackathonRepository,
                participatingTeamRepository
        );
    }

    public void createReport(Long staffProfileId, Long hackathonId, Long participatingTeamId, CreateReportDTO createReportDTO) {
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            reportValidator.validate(createReportDTO, staffProfileId, hackathonId, participatingTeamId);

            Hackathon hackathon = hackathonRepository.getById(hackathonId);
            StaffProfile mentor = staffProfileRepository.getById(staffProfileId);
            ParticipatingTeam team = participatingTeamRepository.getById(participatingTeamId);

            Report report = new Report(
                    hackathon,
                    mentor,
                    team,
                    createReportDTO.getReason(),
                    createReportDTO.getUrgency()
            );

            reportRepository.save(report);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}