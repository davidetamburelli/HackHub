package model.dto.responsedto;

import model.enums.InvitationStatus;

public record InvitationSummaryDTO(
        Long id,
        Long teamId,
        String teamName,
        InvitationStatus status
) {}
