package model.dto.responsedto;


import model.enums.SupportRequestStatus;
import model.enums.Urgency;

public record SupportRequestSummaryDTO(
        Long id,
        Long hackathonId,
        Long participatingTeamId,
        String title,
        SupportRequestStatus status,
        Urgency urgency
) {}