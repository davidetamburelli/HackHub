package model.dto.requestdto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.ReportResolution;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplySanctionDTO {

    @NotNull(message = "Il tipo di sanzione è obbligatorio")
    private ReportResolution sanctionType;

    @NotBlank(message = "La motivazione della sanzione è obbligatoria")
    private String reason;

    @PositiveOrZero(message = "I punti di penalità non possono essere negativi")
    private int points;

    @AssertTrue(message = "Se applichi una decurtazione di punti, i punti devono essere maggiori di zero")
    public boolean isPointsValidForSanction() {
        if (sanctionType == ReportResolution.POINT_DEDUCTION) {
            return points > 0;
        }
        return true;
    }
}