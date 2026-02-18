package validators;

import model.dto.CreateHackathonDTO;
import model.valueobjs.Period;
import repository.HackathonRepository;
import repository.StaffProfileRepository;
import utils.DomainException;

import java.time.LocalDateTime;

public class HackathonValidator {

    private final HackathonRepository hackathonRepository;
    private final StaffProfileRepository staffProfileRepository;

    public HackathonValidator(HackathonRepository hackathonRepository, StaffProfileRepository staffProfileRepository) {
        this.hackathonRepository = hackathonRepository;
        this.staffProfileRepository = staffProfileRepository;
    }

    public void validate(CreateHackathonDTO dto, Long organizerId) {
        // 1. Validazione Input Base
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");
        if (organizerId == null) throw new IllegalArgumentException("L'ID dell'organizzatore è obbligatorio");

        if (dto.getName() == null || dto.getName().trim().isBlank())
            throw new IllegalArgumentException("Il nome dell'hackathon è obbligatorio");

        if (dto.getType() == null || dto.getType().trim().isBlank())
            throw new IllegalArgumentException("Il tipo di hackathon è obbligatorio");

        if (dto.getJudgeEmail() == null || dto.getJudgeEmail().trim().isBlank())
            throw new IllegalArgumentException("L'email del giudice è obbligatoria");

        if (dto.getPrize() < 0)
            throw new IllegalArgumentException("Il premio non può essere negativo");

        if (dto.getMaxTeamSize() <= 0)
            throw new IllegalArgumentException("La dimensione massima del team deve essere maggiore di zero");

        if (hackathonRepository.existsByName(dto.getName())) {
            throw new DomainException("Esiste già un hackathon con questo nome: " + dto.getName());
        }

        if (staffProfileRepository.getById(organizerId) == null) {
            throw new DomainException("Organizzatore non trovato (ID: " + organizerId + ")");
        }

        if (staffProfileRepository.findByEmail(dto.getJudgeEmail()) == null) {
            throw new DomainException("Nessun profilo staff trovato per l'email del giudice: " + dto.getJudgeEmail());
        }

        if (dto.getMentorEmails() != null && !dto.getMentorEmails().isEmpty()) {
            for (String email : dto.getMentorEmails()) {
                if (staffProfileRepository.findByEmail(email) == null) {
                    throw new DomainException("Nessun profilo staff trovato per il mentore: " + email);
                }
            }
        }

        validateDates(dto.getSubscriptionDates(), dto.getDates());
    }

    private void validateDates(Period sub, Period hack) {
        if (sub == null || hack == null) {
            throw new IllegalArgumentException("Le date di iscrizione e svolgimento sono obbligatorie");
        }

        LocalDateTime now = LocalDateTime.now();

        if (sub.getStartAt() == null || sub.getEndAt() == null)
            throw new IllegalArgumentException("Date iscrizione incomplete");

        if (sub.getStartAt().isAfter(sub.getEndAt())) {
            throw new DomainException("La data inizio iscrizioni deve precedere la fine");
        }

        if (hack.getStartAt() == null || hack.getEndAt() == null)
            throw new IllegalArgumentException("Date hackathon incomplete");

        if (hack.getStartAt().isAfter(hack.getEndAt())) {
            throw new DomainException("La data inizio hackathon deve precedere la fine");
        }

        if (sub.getEndAt().isBefore(now)) {
            throw new DomainException("La data di fine iscrizione non può essere nel passato");
        }

        if (hack.getStartAt().isBefore(sub.getStartAt())) {
            throw new DomainException("L'hackathon non può iniziare prima dell'apertura delle iscrizioni");
        }

        if (sub.getEndAt().isAfter(hack.getEndAt())) {
            throw new DomainException("Le iscrizioni non possono terminare dopo la fine dell'hackathon");
        }
    }
}