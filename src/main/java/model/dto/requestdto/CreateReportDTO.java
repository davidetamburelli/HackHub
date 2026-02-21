package model.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotBlank(message = "Devi fornire una motivazione per la segnalazione")
    private String reason;

    @NotNull(message = "L'urgenza della segnalazione è obbligatoria")
    private Urgency urgency;

}