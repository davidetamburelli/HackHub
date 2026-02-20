package utils.adapters;

import model.dto.PaymentResult;
import model.enums.PayoutMethod;

public interface IPayoutAdapter {

    PayoutMethod supports();
    PaymentResult transfer(double amount, String destination);

}