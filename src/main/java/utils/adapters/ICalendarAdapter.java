package utils.adapters;

import model.dto.CallBookingResult;
import utils.facade.CalendarEventSpec;

public interface ICalendarAdapter {

    CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec);

}
