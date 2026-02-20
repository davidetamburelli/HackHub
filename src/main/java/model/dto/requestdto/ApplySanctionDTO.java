package model.dto.requestdto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import model.enums.ReportResolution;

@Getter
@NoArgsConstructor
public class ApplySanctionDTO {

    private ReportResolution sanctionType;
    private String reason;
    private int points;

    public ApplySanctionDTO(ReportResolution sanctionType, String reason, int points) {
        this.sanctionType = sanctionType;
        this.reason = reason;
        this.points = points;
    }
}