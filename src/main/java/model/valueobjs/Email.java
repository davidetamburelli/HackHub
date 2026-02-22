package model.valueobjs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.regex.Pattern;

@Embeddable
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email {

    private static final String EMAIL_PATTERN = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_PATTERN);

    @Column(name = "email", nullable = false)
    private String value;

    public Email(String value) {
        if (value == null || value.trim().isBlank()) {
            throw new IllegalArgumentException("Email address cannot be blank.");
        }

        String cleanEmail = value.trim().toLowerCase();

        if (!PATTERN.matcher(cleanEmail).matches()) {
            throw new IllegalArgumentException("Email format not valid: " + value);
        }

        this.value = cleanEmail;
    }
}