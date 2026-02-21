package model.dto.responsedto;

import java.time.LocalDateTime;

public record WinnerParticipatingTeamDTO(
        Long participatingTeamId,
        Long teamId,
        String contactEmail,
        LocalDateTime registeredAt,
        int totalPenaltyPoints
) {}