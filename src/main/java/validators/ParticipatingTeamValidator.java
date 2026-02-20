package validators;

import model.dto.RegisterTeamDTO;

public class ParticipatingTeamValidator {

    public ParticipatingTeamValidator() {}

    public void validate(RegisterTeamDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO di registrazione non può essere nullo");

        if (dto.getContactEmail() == null || dto.getContactEmail().trim().isBlank()) {
            throw new IllegalArgumentException("L'email di contatto è obbligatoria");
        }
        if (dto.getPayoutMethod() == null) {
            throw new IllegalArgumentException("Il metodo di pagamento è obbligatorio");
        }
        if (dto.getPayoutRef() == null || dto.getPayoutRef().trim().isBlank()) {
            throw new IllegalArgumentException("Il riferimento per il pagamento è obbligatorio");
        }
    }
}