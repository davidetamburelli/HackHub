package model.valueobjs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.enums.PrizeStatus;

import java.time.LocalDateTime;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrizePayout {

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_status", nullable = false)
    private PrizeStatus status;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "provider_ref")
    private String providerRef;

    @Column(name = "failure_reason")
    private String failureReason;

    public PrizePayout(PrizeStatus status, LocalDateTime paidAt, String providerRef, String failureReason) {
        this.status = status;
        this.paidAt = paidAt;
        this.providerRef = providerRef;
        this.failureReason = failureReason;
    }
}