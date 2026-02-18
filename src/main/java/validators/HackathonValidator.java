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

    public void validate(CreateHackathonDTO dto) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");

        if (dto.getName() == null || dto.getName().isBlank())
            throw new IllegalArgumentException("Il nome dell'hackathon è obbligatorio");

        if (dto.getStaffProfileId() == null)
            throw new IllegalArgumentException("L'organizzatore è obbligatorio");

        if (hackathonRepository.existsByName(dto.getName())) {
            throw new DomainException("Esiste già un hackathon con questo nome: " + dto.getName());
        }

        if (staffProfileRepository.getById(dto.getStaffProfileId()) == null) {
            throw new DomainException("Profilo staff organizzatore non trovato (ID: " + dto.getStaffProfileId() + ")");
        }

        validateDates(dto.getSubscriptionDates(), dto.getDates());
    }

    private void validateDates(Period sub, Period hack) {
        if (sub == null || hack == null) {
            throw new IllegalArgumentException("Le date di iscrizione e svolgimento sono obbligatorie");
        }

        LocalDateTime now = LocalDateTime.now();

        if (sub.getEndAt().isBefore(now)) {
            throw new DomainException("La data di fine iscrizione non può essere nel passato");
        }

        if (sub.getStartAt().isAfter(sub.getEndAt())) {
            throw new DomainException("La data inizio iscrizioni deve essere precedente alla fine");
        }

        if (hack.getStartAt().isAfter(hack.getEndAt())) {
            throw new DomainException("La data inizio hackathon deve essere precedente alla fine");
        }

        if (hack.getStartAt().isBefore(sub.getStartAt())) {
            throw new DomainException("L'hackathon non può iniziare prima dell'apertura delle iscrizioni");
        }
    }
}