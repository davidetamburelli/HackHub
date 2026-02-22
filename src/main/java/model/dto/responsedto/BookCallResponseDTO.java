package model.dto.responsedto;

import java.time.LocalDateTime;

public record BookCallResponseDTO(
        Long hackathonId,
        Long supportRequestId,
        String eventId,
        String meetingURL,
        LocalDateTime startsAt,
        // Duration duration,
        //LocalDateTime end,
        String status                 // CREATED / FAILED
) {}
