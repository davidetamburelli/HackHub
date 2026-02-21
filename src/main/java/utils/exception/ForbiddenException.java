package utils.exception;

import java.util.Map;

public class ForbiddenException extends DomainException {
    public ForbiddenException(String message) {
        super("FORBIDDEN", message);
    }

    public ForbiddenException(String message, Map<String, Object> details) {
        super("FORBIDDEN", message, details);
    }
}