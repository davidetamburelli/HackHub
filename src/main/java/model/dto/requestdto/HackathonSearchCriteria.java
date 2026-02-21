package model.dto.requestdto;

import jakarta.validation.constraints.AssertTrue;
import model.enums.HackathonStatus;
import java.time.LocalDate;

public record HackathonSearchCriteria(
        String nameContains,
        HackathonStatus status,
        Boolean isOnline,
        LocalDate startsBefore,
        LocalDate startsAfter
) {

    @AssertTrue(message = "La data 'startsAfter' deve precedere o essere uguale a 'startsBefore'")
    public boolean isDateRangeValid() {
        if (startsBefore == null || startsAfter == null) {
            return true;
        }
        return startsAfter.isBefore(startsBefore) || startsAfter.isEqual(startsBefore);
    }
}