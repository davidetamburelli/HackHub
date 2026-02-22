package model.dto.requestdto;

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
public class CreateTeamDTO {

    @NotBlank(message = "Il nome del team non può essere vuoto")
    @Size(min = 3, max = 50, message = "Il nome del team deve avere tra 3 e 50 caratteri")
    private String name;
}