package handlers;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import model.ParticipatingTeam;
import model.Report;
import model.dto.requestdto.ApplySanctionDTO;
import model.dto.requestdto.CreateReportDTO;
import model.enums.HackathonStatus;
import model.enums.ReportResolution;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.ReportRepository;
import repository.StaffProfileRepository;
import utils.DomainException;
import validators.ReportValidator;

import java.time.LocalDateTime;

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
        this.reportValidator = new ReportValidator();
    }

    public void createReport(Long staffProfileId, Long hackathonId, Long participatingTeamId, CreateReportDTO createReportDTO) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            reportValidator.validate(createReportDTO);

            boolean isMentor = hackathonRepository.existsMentor(hackathonId, staffProfileId);
            if (!isMentor) {
                throw new DomainException("L'utente non è un mentore per questo hackathon");
            }

            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING) {
                throw new DomainException("L'hackathon non è attualmente in corso");
            }

            ParticipatingTeam participatingTeam = participatingTeamRepository.getByIdAndHackathonId(participatingTeamId, hackathonId);
            if (participatingTeam == null) {
                throw new DomainException("Team partecipante non trovato");
            }

            Report createdReport = new Report(
                    hackathonId,
                    staffProfileId,
                    participatingTeamId,
                    createReportDTO.getReason(),
                    createReportDTO.getUrgency(),
                    LocalDateTime.now()
            );

            reportRepository.save(createdReport);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void applySanction(Long staffProfileId, Long hackathonId, Long reportId, ApplySanctionDTO applySanctionDTO) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            reportValidator.validate(applySanctionDTO);
            boolean isOrganizer = hackathonRepository.existsOrganizer(hackathonId, staffProfileId);
            if (!isOrganizer) {
                throw new DomainException("Operazione non autorizzata");
            }
            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING && hackathonStatus != HackathonStatus.IN_EVALUATION) {
                throw new DomainException("Stato hackathon non valido per sanzioni");
            }
            Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
            if (report == null) {
                throw new DomainException("Segnalazione non trovata");
            }
            ParticipatingTeam pt = participatingTeamRepository.getByIdAndHackathonId(report.getParticipatingTeam(), hackathonId);
            if (report.isResolved()) {
                throw new DomainException("Segnalazione già risolta");
            }
            if (applySanctionDTO.getSanctionType() == ReportResolution.POINT_DEDUCTION) {
                pt.applyPenalty(applySanctionDTO.getPoints(), applySanctionDTO.getReason(), report.getId());
            } else if (applySanctionDTO.getSanctionType() == ReportResolution.TEAM_DISQUALIFICATION) {
                pt.disqualify();
            }
            report.resolve(applySanctionDTO.getSanctionType());
            participatingTeamRepository.save(pt);
            reportRepository.save(report);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }

    public void archiveReport(Long staffProfileId, Long hackathonId, Long reportId) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            boolean isOrganizer = hackathonRepository.existsOrganizer(hackathonId, staffProfileId);
            if (!isOrganizer) {
                throw new DomainException("Operazione non autorizzata");
            }
            HackathonStatus hackathonStatus = hackathonRepository.findStatusByHackathonId(hackathonId);
            if (hackathonStatus != HackathonStatus.RUNNING && hackathonStatus != HackathonStatus.IN_EVALUATION) {
                throw new DomainException("Stato hackathon non valido");
            }
            Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
            if (report == null) {
                throw new DomainException("Segnalazione non trovata");
            }
            if (report.isResolved()) {
                throw new DomainException("Segnalazione già archiviata");
            }
            report.archive();
            reportRepository.save(report);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        }
    }
}