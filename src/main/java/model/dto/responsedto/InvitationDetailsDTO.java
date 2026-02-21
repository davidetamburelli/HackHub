package model.dto.responsedto;


import model.enums.InvitationStatus;

public record InvitationDetailsDTO(
        Long id,
        InvitationStatus status,
        TeamRefDTO team
) {}
