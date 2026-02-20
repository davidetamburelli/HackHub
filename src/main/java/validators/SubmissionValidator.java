package validators;

import model.dto.AddSubmissionDTO;

public class SubmissionValidator {

    public SubmissionValidator() {}

    public void validate(AddSubmissionDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("DTO nullo");
        }

        if (dto.getResponse() == null || dto.getResponse().isBlank()) {
            throw new IllegalArgumentException("Testo risposta mancante");
        }
    }
}