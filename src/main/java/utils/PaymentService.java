package utils;

import org.springframework.stereotype.Service;
import utils.adapters.IPayoutAdapter;
import model.dto.requestdto.PaymentResult;
import model.valueobjs.PayoutAccountRef;
import java.util.List;

@Service
public class PaymentService implements IPaymentService {

    private final List<IPayoutAdapter> adapters;

    public PaymentService(List<IPayoutAdapter> adapters) {
        this.adapters = adapters;
    }

    @Override
    public PaymentResult transfer(double amount, PayoutAccountRef destination) {

        if (destination == null || destination.getMethod() == null || destination.getValue() == null) {
            return PaymentResult.fail("Dati di destinazione del pagamento mancanti o incompleti");
        }

        for (IPayoutAdapter adapter : adapters) {
            if (adapter.supports() == destination.getMethod()) {
                return adapter.transfer(amount, destination.getValue());
            }
        }

        return PaymentResult.fail("Nessun servizio di pagamento configurato per il metodo: " + destination.getMethod());
    }
}