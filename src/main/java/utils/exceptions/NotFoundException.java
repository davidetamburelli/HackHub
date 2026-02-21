package utils.exceptions;

import java.util.Map;

public class NotFoundException extends DomainException {
    public NotFoundException(String resource, Object id) {
        super("NOT_FOUND", resource + " not found",
                Map.of("resource", resource, "id", id));
    }

    public NotFoundException(String message, Map<String, Object> details) {
        super("NOT_FOUND", message, details);
    }
}