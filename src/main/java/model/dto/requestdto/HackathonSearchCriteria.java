package model.dto.requestdto;

import model.enums.HackathonStatus;
import java.time.LocalDate;

public record HackathonSearchCriteria(
        String nameContains,
        HackathonStatus status,
        Boolean isOnline,
        LocalDate startsBefore,
        LocalDate startsAfter
) {
}