package validators;

import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.dto.CreateReportDTO;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.StaffProfileRepository;
import utils.DomainException;

public class ReportValidator {

    private final StaffProfileRepository staffProfileRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;

    public ReportValidator(
            StaffProfileRepository staffProfileRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository
    ) {
        this.staffProfileRepository = staffProfileRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
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

        hackathon.assertStaff(mentor);

        if (!team.getHackathon().getId().equals(hackathon.getId())) {
            throw new DomainException("Il team selezionato non partecipa all'hackathon specificato.");
        }
    }
}