package model.dto.responsedto;

import java.time.LocalDateTime;

public record SubmissionDetailsDTO(
        Long id,
        Long hackathonId,
        Long participatingTeamId,
        String response,
        String responseUrl,
        LocalDateTime updatedAt,
        EvaluationDTO evaluation  // null se non valutata
) {}
