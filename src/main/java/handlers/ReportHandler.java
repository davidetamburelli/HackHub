package handlers;

import model.ParticipatingTeam;
import model.Report;
import model.dto.requestdto.ApplySanctionDTO;
import model.dto.requestdto.CreateReportDTO;
import model.enums.HackathonStatus;
import model.enums.ReportResolution;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.ReportRepository;
import utils.DomainException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReportHandler {

    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ReportRepository reportRepository;

    public ReportHandler(
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository,
            ReportRepository reportRepository) {
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.reportRepository = reportRepository;
    }

    @Transactional
    public void createReport(Long staffProfileId, Long hackathonId, Long participatingTeamId, CreateReportDTO createReportDTO) {
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
    }

    public List<Report> getReports(Long staffProfileId, Long hackathonId) {
        boolean isOrganizer = hackathonRepository.existsMentor(hackathonId, staffProfileId);
        if (!isOrganizer) {
            throw new DomainException("Operazione non autorizzata: non sei l'organizzatore dell'hackathon");
        }

        return reportRepository.getByHackathonId(hackathonId);
    }

    public Report getReportDetails(Long staffProfileId, Long hackathonId, Long reportId) {
        boolean isOrganizer = hackathonRepository.existsMentor(hackathonId, staffProfileId);
        if (!isOrganizer) {
            throw new DomainException("Operazione non autorizzata: non sei l'organizzatore dell'hackathon");
        }

        Report report = reportRepository.getByIdAndHackathonId(reportId, hackathonId);
        if (report == null) {
            throw new DomainException("La segnalazione non appartiene all'hackathon selezionato");
        }

        return report;
    }

    @Transactional
    public void applySanction(Long staffProfileId, Long hackathonId, Long reportId, ApplySanctionDTO applySanctionDTO) {
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
    }

    @Transactional
    public void archiveReport(Long staffProfileId, Long hackathonId, Long reportId) {
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
    }
}