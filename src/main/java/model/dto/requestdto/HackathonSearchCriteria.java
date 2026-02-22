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

    @Override
    public String nameContains() {
        return nameContains;
    }

    @Override
    public HackathonStatus status() {
        return status;
    }

    @Override
    public Boolean isOnline() {
        return isOnline;
    }

    @Override
    public LocalDate startsBefore() {
        return startsBefore;
    }

    @Override
    public LocalDate startsAfter() {
        return startsAfter;
    }
}