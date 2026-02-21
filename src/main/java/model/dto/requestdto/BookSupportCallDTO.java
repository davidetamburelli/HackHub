package model.dto.requestdto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookSupportCallDTO {

    @NotNull(message = "La data e l'ora di inizio sono obbligatorie")
    @Future(message = "Non puoi prenotare una call nel passato")
    private LocalDateTime startsAt;

    @NotNull(message = "La durata della call è obbligatoria")
    private Duration duration;

    @NotBlank(message = "Il titolo della call è obbligatorio")
    private String title;

    @NotBlank(message = "La descrizione del problema è obbligatoria")
    private String description;

}