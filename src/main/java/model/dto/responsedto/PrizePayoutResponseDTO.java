package model.dto.responsedto;

import model.enums.PrizeStatus;

import java.time.LocalDateTime;

public record PrizePayoutResponseDTO(
        Long hackathonId,
        PrizeStatus status,
        LocalDateTime paidAt,
        String providerRef,
        String failureReason
) {}
