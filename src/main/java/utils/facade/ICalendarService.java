package utils.facade;

import model.dto.CallBookingRequest;
import model.dto.requestdto.CallBookingResult;

public interface ICalendarService {

    CallBookingResult scheduleCall(CallBookingRequest request);

}