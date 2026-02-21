package model.dto.responsedto;

import model.enums.PrizeStatus;

import java.time.LocalDateTime;

public record PrizePayoutDTO(
        PrizeStatus status,
        LocalDateTime paidAt,
        String providerRef,
        String failureReason
) {}