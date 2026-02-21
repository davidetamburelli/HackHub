package model.dto;

import java.time.Duration;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CallBookingRequest {

    private Long mentor;

    private String title;

    private String description;

    private LocalDateTime startsAt;

    private Duration duration;

    private String attendeeEmail;

    public CallBookingRequest(
            Long mentor,
            String title,
            String description,
            LocalDateTime startsAt,
            Duration duration,
            String attendeeEmail
    ) {
        this.mentor = mentor;
        this.title = title;
        this.description = description;
        this.startsAt = startsAt;
        this.duration = duration;
        this.attendeeEmail = attendeeEmail;
    }
    
}