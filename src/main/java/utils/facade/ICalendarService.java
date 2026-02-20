package utils.facade;

import model.dto.CallBookingRequest;
import model.dto.CallBookingResult;

public interface ICalendarService {

    CallBookingResult scheduleCall(CallBookingRequest request);

}