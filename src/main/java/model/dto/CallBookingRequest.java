package model.dto;

import jakarta.validation.constraints.Email;
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
public class CallBookingRequest {

    @NotNull(message = "L'ID del mentore è obbligatorio")
    private Long mentor;

    @NotBlank(message = "Il titolo della call non può essere vuoto")
    private String title;

    @NotBlank(message = "La descrizione della call non può essere vuota")
    private String description;

    @NotNull(message = "La data e ora di inizio sono obbligatorie")
    @Future(message = "La call deve essere programmata in una data futura")
    private LocalDateTime startsAt;

    @NotNull(message = "La durata della call è obbligatoria")
    private Duration duration;

    @NotBlank(message = "L'email del partecipante è obbligatoria")
    @Email(message = "Il formato dell'email fornita non è valido")
    private String attendeeEmail;

}