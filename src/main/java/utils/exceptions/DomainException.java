package utils.exceptions;

import java.util.Collections;
import java.util.Map;

public abstract class DomainException extends RuntimeException {
    private final String code;
    private final Map<String, Object> details;

    protected DomainException(String code, String message) {
        this(code, message, null, Collections.emptyMap());
    }

    protected DomainException(String code, String message, Map<String, Object> details) {
        this(code, message, null, details);
    }

    protected DomainException(String code, String message, Throwable cause, Map<String, Object> details) {
        super(message, cause);
        this.code = code;
        this.details = (details == null) ? Collections.emptyMap() : Collections.unmodifiableMap(details);
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}
