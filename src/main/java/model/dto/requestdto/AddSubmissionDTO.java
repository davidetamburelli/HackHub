package model.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddSubmissionDTO {

    @NotBlank(message = "La descrizione del progetto è obbligatoria")
    private String response;

    @URL(message = "L'URL fornito non è in un formato valido (es. https://...)")
    private String responseURL;

}