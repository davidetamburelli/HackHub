package utils.adapters;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;

import utils.adapters.ICalendarAdapter;
import utils.facade.CalendarEventSpec;
import model.dto.CallBookingResult;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;

public class GoogleCalendarAdapter implements ICalendarAdapter {

    private static final String APPLICATION_NAME = "Hackathon Manager";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Override
    public CallBookingResult createMeetEvent(String accessToken, String calendarId, CalendarEventSpec spec) {
        try {

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            AccessToken authAccessToken = new AccessToken(accessToken, null);
            GoogleCredentials credentials = GoogleCredentials.create(authAccessToken);
            HttpRequestInitializer requestInitializer = new HttpCredentialsAdapter(credentials);

            Calendar service = new Calendar.Builder(httpTransport, JSON_FACTORY, requestInitializer)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            Event event = new Event()
                    .setSummary(spec.getTitle())
                    .setDescription(spec.getDescription());

            ZoneId zoneId = ZoneId.systemDefault();

            ZonedDateTime startZdt = spec.getStartsAt().atZone(zoneId);
            DateTime startDateTime = new DateTime(startZdt.toInstant().toEpochMilli());
            EventDateTime start = new EventDateTime().setDateTime(startDateTime).setTimeZone(zoneId.getId());
            event.setStart(start);

            ZonedDateTime endZdt = spec.getStartsAt().plus(spec.getDuration()).atZone(zoneId);
            DateTime endDateTime = new DateTime(endZdt.toInstant().toEpochMilli());
            EventDateTime end = new EventDateTime().setDateTime(endDateTime).setTimeZone(zoneId.getId());
            event.setEnd(end);

            EventAttendee[] attendees = new EventAttendee[] {
                    new EventAttendee().setEmail(spec.getInviteeEmail())
            };
            event.setAttendees(java.util.Arrays.asList(attendees));

            ConferenceSolutionKey conferenceSolutionKey = new ConferenceSolutionKey().setType("hangoutsMeet");
            CreateConferenceRequest createConferenceRequest = new CreateConferenceRequest()
                    .setRequestId(UUID.randomUUID().toString())
                    .setConferenceSolutionKey(conferenceSolutionKey);
            ConferenceData conferenceData = new ConferenceData().setCreateRequest(createConferenceRequest);
            event.setConferenceData(conferenceData);

            Event createdEvent = service.events().insert(calendarId, event)
                    .setConferenceDataVersion(1)
                    .setSendUpdates("all")
                    .execute();

            String eventId = createdEvent.getId();
            String meetLink = createdEvent.getHangoutLink();

            return CallBookingResult.ok(eventId, meetLink);

        } catch (Exception e) {
            e.printStackTrace();
            return CallBookingResult.fail("Errore durante la creazione dell'evento su Google Calendar: " + e.getMessage());
        }
    }
}