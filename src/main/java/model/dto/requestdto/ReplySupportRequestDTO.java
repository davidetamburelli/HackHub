package model.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReplySupportRequestDTO {

    @NotBlank(message = "Il messaggio di risposta non può essere vuoto")
    private String message;

}