package model.dto.responsedto;

import model.enums.HackathonStatus;

public record DeclareWinnerResponseDTO(
        Long hackathonId,
        HackathonStatus status,
        WinnerParticipatingTeamDTO winner
) {}