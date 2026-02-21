package model.dto.responsedto;

import java.time.LocalDateTime;

public record SubmissionSummaryDTO(
        Long id,
        Long hackathonId,
        Long participatingTeamId,
        LocalDateTime updatedAt,
        boolean evaluated,
        Integer score         // null se non valutata
) {}