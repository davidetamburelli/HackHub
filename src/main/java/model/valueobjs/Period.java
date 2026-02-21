package model.valueobjs;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.AssertTrue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Embeddable
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Period {

    @NotNull(message = "La data di inizio è obbligatoria")
    private LocalDate startDate;

    @NotNull(message = "La data di fine è obbligatoria")
    private LocalDate endDate;

    public Period(LocalDate startDate, LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @AssertTrue(message = "La data di fine deve essere successiva o uguale alla data di inizio")
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return endDate.isAfter(startDate) || endDate.isEqual(startDate);
    }
}