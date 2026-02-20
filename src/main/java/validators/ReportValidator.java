package validators;

import model.Hackathon;
import model.ParticipatingTeam;
import model.Report;
import model.StaffProfile;
import model.dto.ApplySanctionDTO;
import model.dto.CreateReportDTO;
import model.enums.ReportResolution;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.ReportRepository;
import repository.StaffProfileRepository;
import utils.DomainException;

public class ReportValidator {

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final ReportRepository reportRepository;

    public ReportValidator(
            StaffProfileRepository staffProfileRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository,
            ReportRepository reportRepository
    ) {
        this.staffProfileRepository = staffProfileRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.reportRepository = reportRepository;
    }

    public void validate(CreateReportDTO dto, Long staffId, Long hackathonId, Long participatingTeamId) {

        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (staffId == null || hackathonId == null || participatingTeamId == null) {
            throw new IllegalArgumentException("Gli ID di Staff, Hackathon e Team sono obbligatori");
        }

        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della segnalazione è obbligatorio");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("Il livello di urgenza è obbligatorio");
        }

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        StaffProfile mentor = staffProfileRepository.getById(staffId);
        if (mentor == null) throw new DomainException("Mentore non trovato");

        ParticipatingTeam team = participatingTeamRepository.getById(participatingTeamId);
        if (team == null) throw new DomainException("Team partecipante non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffId) ||
                hackathon.getJudge().equals(staffId) ||
                hackathon.getMentors().contains(staffId);

        if (!isStaff) {
            throw new DomainException("Operazione non autorizzata: l'utente non fa parte dello staff di questo hackathon");
        }

        if (!team.getHackathon().equals(hackathon.getId())) {
            throw new DomainException("Il team selezionato non partecipa all'hackathon specificato.");
        }
    }

    public void validate(ApplySanctionDTO dto, Long staffId, Long reportId) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (staffId == null || reportId == null) {
            throw new IllegalArgumentException("Gli ID di Staff e Report sono obbligatori");
        }

        if (dto.getSanctionType() == null) {
            throw new IllegalArgumentException("Il tipo di sanzione è obbligatorio");
        }
        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della sanzione è obbligatorio");
        }

        if (dto.getSanctionType() == ReportResolution.POINT_DEDUCTION && dto.getPoints() <= 0) {
            throw new IllegalArgumentException("I punti di penalità devono essere maggiori di zero per una decurtazione");
        }

        Report report = reportRepository.getById(reportId);
        if (report == null) throw new DomainException("Segnalazione non trovata");

        if (report.isResolved()) {
            throw new DomainException("Questa segnalazione è già stata risolta in precedenza");
        }

        Hackathon hackathon = hackathonRepository.getById(report.getHackathon());
        if (hackathon == null) throw new DomainException("Hackathon associato non trovato");

        boolean canSanction = hackathon.getOrganizer().equals(staffId) || hackathon.getJudge().equals(staffId);
        if (!canSanction) {
            throw new DomainException("Operazione non autorizzata: solo l'organizzatore o il giudice possono applicare sanzioni disciplinari");
        }
    }
}