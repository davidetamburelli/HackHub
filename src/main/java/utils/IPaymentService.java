package utils;

import model.dto.PaymentResult;
import model.valueobjs.PayoutAccountRef;

public interface IPaymentService {

    PaymentResult transfer(double amount, PayoutAccountRef destination);

}