package validators;

import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.SupportRequest;
import model.Team;
import model.User;
import model.dto.CreateSupportRequestDTO;
import model.dto.ReplySupportRequestDTO;
import repository.HackathonRepository;
import repository.ParticipatingTeamRepository;
import repository.StaffProfileRepository;
import repository.SupportRequestRepository;
import repository.TeamRepository;
import repository.UserRepository;
import utils.DomainException;

public class SupportRequestValidator {

    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final HackathonRepository hackathonRepository;
    private final ParticipatingTeamRepository participatingTeamRepository;
    private final SupportRequestRepository supportRequestRepository;
    private final StaffProfileRepository staffProfileRepository;

    public SupportRequestValidator(
            UserRepository userRepository,
            TeamRepository teamRepository,
            HackathonRepository hackathonRepository,
            ParticipatingTeamRepository participatingTeamRepository,
            SupportRequestRepository supportRequestRepository,
            StaffProfileRepository staffProfileRepository
    ) {
        this.userRepository = userRepository;
        this.teamRepository = teamRepository;
        this.hackathonRepository = hackathonRepository;
        this.participatingTeamRepository = participatingTeamRepository;
        this.supportRequestRepository = supportRequestRepository;
        this.staffProfileRepository = staffProfileRepository;
    }

    // 1. Validazione per la Creazione del Ticket
    public void validate(CreateSupportRequestDTO dto, Long userId, Long hackathonId) {

        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (userId == null || hackathonId == null) throw new IllegalArgumentException("ID Utente e Hackathon sono obbligatori");

        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della richiesta è obbligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("L'urgenza è obbligatoria");
        }

        User user = userRepository.getById(userId);
        if (user == null) throw new DomainException("Utente non trovato");

        Hackathon hackathon = hackathonRepository.getById(hackathonId);
        if (hackathon == null) throw new DomainException("Hackathon non trovato");

        Team team = teamRepository.findByMemberId(userId);
        if (team == null) {
            throw new DomainException("L'utente non appartiene a nessun team.");
        }

        // Logica migrata da team.assertLeader(user)
        if (!team.getLeader().equals(userId)) {
            throw new DomainException("Solo il leader del team può aprire una richiesta di supporto.");
        }

        ParticipatingTeam pt = participatingTeamRepository.findByHackathonIdAndTeamId(hackathonId, team.getId());
        if (pt == null) {
            throw new DomainException("Il team non è iscritto a questo hackathon. Non è possibile aprire ticket.");
        }
    }

    // 2. NUOVO METODO: Validazione per la Risposta al Ticket
    public void validate(ReplySupportRequestDTO dto, Long staffId, Long supportRequestId) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (staffId == null || supportRequestId == null) {
            throw new IllegalArgumentException("Gli ID di Staff e SupportRequest sono obbligatori");
        }

        if (dto.getMessage() == null || dto.getMessage().trim().isBlank()) {
            throw new IllegalArgumentException("Il messaggio di risposta è obbligatorio");
        }

        SupportRequest request = supportRequestRepository.getById(supportRequestId);
        if (request == null) throw new DomainException("Richiesta di supporto non trovata");

        if (request.getReply() != null) {
            throw new DomainException("Questa richiesta di supporto ha già ricevuto una risposta");
        }

        StaffProfile staff = staffProfileRepository.getById(staffId);
        if (staff == null) throw new DomainException("Profilo staff non trovato");

        Hackathon hackathon = hackathonRepository.getById(request.getHackathon());
        if (hackathon == null) throw new DomainException("Hackathon associato non trovato");

        boolean isStaff = hackathon.getOrganizer().equals(staffId) ||
                hackathon.getJudge().equals(staffId) ||
                hackathon.getMentors().contains(staffId);

        if (!isStaff) {
            throw new DomainException("Operazione non autorizzata: solo lo staff dell'hackathon può rispondere ai ticket");
        }
    }
}