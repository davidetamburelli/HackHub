package model.dto.responsedto;

public record UserSummaryDTO(
        Long id,
        String username,
        String name,
        String surname,
        Long teamId, //forse
        boolean alreadyInTeam
) {}