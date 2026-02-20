package utils.facade;

import model.dto.CallBookingRequest;
import model.dto.requestdto.CallBookingResult;
import utils.adapters.ICalendarAdapter;

public class CalendarFacade implements ICalendarService {

    private final GoogleOAuthTokenStore tokenStore;
    private final ICalendarAdapter calendarAdapter;

    public CalendarFacade(GoogleOAuthTokenStore tokenStore, ICalendarAdapter calendarAdapter) {
        this.tokenStore = tokenStore;
        this.calendarAdapter = calendarAdapter;
    }

    @Override
    public CallBookingResult scheduleCall(CallBookingRequest request) {

        if (request == null) {
            return CallBookingResult.fail("La richiesta di prenotazione è nulla");
        }

        CalendarEventSpec spec = new CalendarEventSpec(
                request.getTitle(),
                request.getDescription(),
                request.getStartsAt(),
                request.getDuration(),
                request.getAttendeeEmail()
        );

        String accessToken = tokenStore.findAccessTokenByStaffProfileId(request.getMentor());

        if (accessToken == null || accessToken.isBlank()) {
            return CallBookingResult.fail("Token di accesso non trovato per il Mentor. Il Mentor deve prima collegare il proprio account Google.");
        }

        return calendarAdapter.createMeetEvent(accessToken, "primary", spec);
    }
}