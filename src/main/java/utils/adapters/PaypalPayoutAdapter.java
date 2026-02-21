package utils.adapters;

import model.dto.requestdto.PaymentResult;
import model.enums.PayoutMethod;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PaypalPayoutAdapter implements IPayoutAdapter {

    @Override
    public PayoutMethod supports() {
        return PayoutMethod.PAYPAL;
    }

    @Override
    public PaymentResult transfer(double amount, String destination) {
        if (destination == null || !destination.contains("@")) {
            return PaymentResult.fail("Indirizzo email PayPal non valido");
        }

        System.out.println("[SIMULAZIONE API PAYPAL] Invio fondi di " + amount + "€ all'account: " + destination);

        String providerRef = "PAYPAL-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        return PaymentResult.ok(providerRef);
    }
}