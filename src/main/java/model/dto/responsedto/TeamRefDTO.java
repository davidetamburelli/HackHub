package model.dto.responsedto;

public record TeamRefDTO(
        Long id,
        String name,
        Long leaderUserId
) {}