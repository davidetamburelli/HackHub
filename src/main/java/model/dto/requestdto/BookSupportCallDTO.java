package model.dto.requestdto;

import java.time.Duration;
import java.time.LocalDateTime;

public class BookSupportCallDTO {

    private LocalDateTime startsAt;
    private Duration duration;
    private String title;
    private String description;

    public BookSupportCallDTO(
            LocalDateTime startsAt,
            Duration duration,
            String title,
            String description
    ) {
        this.startsAt = startsAt;
        this.duration = duration;
        this.title = title;
        this.description = description;
    }

    public LocalDateTime getStartsAt() {
        return startsAt;
    }

    public Duration getDuration() {
        return duration;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}