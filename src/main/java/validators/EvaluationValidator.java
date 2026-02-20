package validators;

import model.dto.requestdto.AddEvaluationDTO;

public class EvaluationValidator {

    public EvaluationValidator() {}

    public void validate(AddEvaluationDTO dto, Long staffId, Long submissionId) {
        if (dto == null) throw new IllegalArgumentException("DTO nullo");
        if (staffId == null || submissionId == null) throw new IllegalArgumentException("ID mancanti");

        if (dto.getScore() < 0 || dto.getScore() > 10) {
            throw new IllegalArgumentException("Score non valido (0-10)");
        }
    }
}