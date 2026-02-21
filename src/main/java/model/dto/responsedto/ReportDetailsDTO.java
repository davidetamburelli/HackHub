package model.dto.responsedto;


import model.enums.ReportResolution;
import model.enums.Urgency;

import java.time.LocalDateTime;

public record ReportDetailsDTO(
        Long id,
        Long hackathonId,
        ParticipatingTeamRefDTO participatingTeam,
        StaffRefDTO mentor,
        String reason,
        Urgency urgency,
        LocalDateTime createdAt,
        boolean resolved,
        ReportResolution resolution // null se non risolto
) {}