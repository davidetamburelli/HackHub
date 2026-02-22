package model.dto.requestdto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CallBookingResult {

    private boolean success;
    private String eventId;
    private String meetingURL;
    private String failureReason;

    public boolean isSuccess() {
        return success;
    }

    public static CallBookingResult ok(String eventId, String meetingURL) {
        return new CallBookingResult(true, eventId, meetingURL, null);
    }

    public static CallBookingResult fail(String reason) {
        return new CallBookingResult(false, null, null, reason);
    }
}