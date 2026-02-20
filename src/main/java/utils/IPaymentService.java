package utils;

import model.dto.requestdto.PaymentResult;
import model.valueobjs.PayoutAccountRef;

public interface IPaymentService {

    PaymentResult transfer(double amount, PayoutAccountRef destination);

}