package utils.exceptions;

import java.util.List;
import java.util.Map;

public class InvalidHackathonStateException extends DomainException {

    public InvalidHackathonStateException(String operation, String currentState, List<String> allowedStates) {
        super("HACKATHON_INVALID_STATE",
                "Operation '" + operation + "' is not allowed in state '" + currentState + "'",
                Map.of(
                        "operation", operation,
                        "currentState", currentState,
                        "allowedStates", allowedStates
                ));
    }
}