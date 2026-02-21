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
public class CreateSupportRequestDTO {

    @NotBlank(message = "Il titolo della richiesta è obbligatorio")
    private String title;

    @NotBlank(message = "La descrizione del problema è obbligatoria")
    private String description;

    @NotNull(message = "L'urgenza della richiesta è obbligatoria")
    private Urgency urgency;

}