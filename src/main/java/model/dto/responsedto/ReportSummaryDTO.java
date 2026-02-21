package model.dto.responsedto;

import model.enums.ReportResolution;
import model.enums.Urgency;

import java.time.LocalDateTime;

public record ReportSummaryDTO(
        Long id,
        Long hackathonId,
        Long participatingTeamId,
        Long mentorId,
        Urgency urgency,
        LocalDateTime createdAt,
        boolean resolved,
        ReportResolution resolution // null se !resolved
) {}