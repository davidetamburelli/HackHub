package model.dto.responsedto;

public record ParticipatingTeamRefDTO(
        Long id,
        Long teamId,
        String contactEmail,
        boolean disqualified,
        int totalPenaltyPoints
) {}