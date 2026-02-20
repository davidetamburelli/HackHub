package validators;

import model.dto.requestdto.CreateTeamDTO;

public class TeamValidator {

    public TeamValidator() {}

    public void validate(CreateTeamDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getName() == null || dto.getName().trim().isBlank()) {
            throw new IllegalArgumentException("Il nome del team è obbligatorio");
        }
    }
}