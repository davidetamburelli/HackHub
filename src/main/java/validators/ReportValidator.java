package validators;

import model.dto.ApplySanctionDTO;
import model.dto.CreateReportDTO;
import model.enums.ReportResolution;

public class ReportValidator {

    public ReportValidator() {}

    public void validate(CreateReportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della segnalazione è obbligatorio");
        }
        if (dto.getUrgency() == null) {
            throw new IllegalArgumentException("Il livello di urgenza è obbligatorio");
        }
    }

    public void validate(ApplySanctionDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Il DTO non può essere nullo");

        if (dto.getSanctionType() == null) {
            throw new IllegalArgumentException("Il tipo di sanzione è obbligatorio");
        }
        if (dto.getReason() == null || dto.getReason().trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della sanzione è obbligatorio");
        }

        if (dto.getSanctionType() == ReportResolution.POINT_DEDUCTION && dto.getPoints() <= 0) {
            throw new IllegalArgumentException("I punti di penalità devono essere maggiori di zero per una decurtazione");
        }
    }
}