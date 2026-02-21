package utils.exceptions;

import java.util.Map;

public class DomainRuleViolationException extends DomainException {
    public DomainRuleViolationException(String code, String message) {
        super(code, message);
    }

    public DomainRuleViolationException(String code, String message, Map<String, Object> details) {
        super(code, message, details);
    }
}
