package utils.adapters;

import model.dto.PaymentResult;
import model.enums.PayoutMethod;

import java.util.UUID;

public class IbanPayoutAdapter implements IPayoutAdapter {

    @Override
    public PayoutMethod supports() {
        return PayoutMethod.IBAN;
    }

    @Override
    public PaymentResult transfer(double amount, String destination) {
        if (destination == null || destination.isBlank()) {
            return PaymentResult.fail("IBAN di destinazione mancante o non valido");
        }

        System.out.println("[SIMULAZIONE API BANCA] Esecuzione bonifico SEPA di " + amount + "€ verso l'IBAN: " + destination);

        String providerRef = "TRX-IBAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        return PaymentResult.ok(providerRef);
    }
}