package model.dto.requestdto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateHackathonDTO {

    @NotBlank(message = "Il nome dell'hackathon è obbligatorio")
    @Size(min = 3, max = 100, message = "Il nome deve essere tra 3 e 100 caratteri")
    private String name;

    @NotBlank(message = "Il tipo di hackathon è obbligatorio")
    private String type;

    @NotBlank(message = "Il regolamento è obbligatorio")
    private String regulation;

    private boolean isOnline;

    private String location;

    @NotBlank(message = "Le modalità di consegna sono obbligatorie")
    private String delivery;

    @PositiveOrZero(message = "Il montepremi non può essere negativo")
    private double prize;

    @Min(value = 1, message = "La dimensione massima del team deve essere almeno 1")
    private int maxTeamSize;

    @NotNull(message = "Il periodo di iscrizione è obbligatorio")
    @Valid
    private Period subscriptionDates;

    @NotNull(message = "Le date di svolgimento sono obbligatorie")
    @Valid
    private Period dates;

    @NotBlank(message = "L'email del giudice è obbligatoria")
    @Email(message = "L'email del giudice non ha un formato valido")
    private String judgeEmail;

    @NotNull(message = "La lista dei mentori non può essere nulla")
    private List<@NotBlank @Email(message = "Una o più email dei mentori non sono valide") String> mentorEmails;

    @NotNull(message = "La politica di classifica è obbligatoria")
    private RankingPolicy rankingPolicy;

    @AssertTrue(message = "L'hackathon non può iniziare prima della fine delle iscrizioni")
    public boolean isChronologicalOrderValid() {
        if (subscriptionDates == null || dates == null ||
                subscriptionDates.getEndDate() == null || dates.getStartDate() == null) {
            return true;
        }

        return dates.getStartDate().isAfter(subscriptionDates.getEndDate()) ||
                dates.getStartDate().isEqual(subscriptionDates.getEndDate());
    }

}