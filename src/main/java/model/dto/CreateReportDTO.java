package model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.Urgency;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateReportDTO {

    private String reason;
    private Urgency urgency;

}