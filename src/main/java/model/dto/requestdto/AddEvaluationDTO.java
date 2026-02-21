package model.dto.requestdto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddEvaluationDTO {

    @Min(value = 0, message = "Il punteggio non può essere inferiore a 0")
    @Max(value = 100, message = "Il punteggio non può essere superiore a 100")
    private int score;

    @NotBlank(message = "Il commento alla valutazione è obbligatorio")
    @Size(max = 1000, message = "Il commento non può superare i 1000 caratteri")
    private String comment;

}