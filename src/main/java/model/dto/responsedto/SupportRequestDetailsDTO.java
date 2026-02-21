package model.dto.responsedto;

import model.enums.SupportRequestStatus;
import model.enums.Urgency;

import java.time.LocalDateTime;

public record SupportRequestDetailsDTO(
        Long id,
        Long hackathonId,
        ParticipatingTeamRefDTO requesterTeam,
        SupportRequestStatus status,
        Urgency urgency,
        String title,
        String description,
        LocalDateTime createdAt
) {}
