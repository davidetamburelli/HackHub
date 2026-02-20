package model.valueobjs;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.enums.PayoutMethod;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PayoutAccountRef {

    @Enumerated(EnumType.STRING)
    @Column(name = "payout_method")
    private PayoutMethod method;

    @Column(name = "payout_value")
    private String value;

    public PayoutAccountRef(PayoutMethod method, String value) {
        this.method = method;
        this.value = value;
    }
}