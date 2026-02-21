package utils.adapters;

import model.dto.requestdto.PaymentResult;
import model.enums.PayoutMethod;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class StripeAccountPayoutAdapter implements IPayoutAdapter {

    @Override
    public PayoutMethod supports() {
        return PayoutMethod.STRIPE_ACCOUNT;
    }

    @Override
    public PaymentResult transfer(double amount, String destination) {
        if (destination == null || !destination.startsWith("acct_")) {
            return PaymentResult.fail("L'ID dell'account Stripe deve iniziare con 'acct_'");
        }

        System.out.println("[SIMULAZIONE API STRIPE] Trasferimento di " + amount + "€ verso l'account connesso: " + destination);

        String providerRef = "tr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        return PaymentResult.ok(providerRef);
    }
}