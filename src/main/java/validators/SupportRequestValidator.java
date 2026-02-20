package validators;

import model.dto.CreateSupportRequestDTO;
import model.dto.ReplySupportRequestDTO;

public class SupportRequestValidator {

    public SupportRequestValidator() {}

    public void validate(CreateSupportRequestDTO dto) {

        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getTitle() == null || dto.getTitle().trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della richiesta è obbligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione è obbligatoria");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("L'urgenza è obbligatoria");
        }
    }

    public void validate(ReplySupportRequestDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getMessage() == null || dto.getMessage().trim().isBlank()) {
            throw new IllegalArgumentException("Il messaggio di risposta è obbligatorio");
        }
    }
}