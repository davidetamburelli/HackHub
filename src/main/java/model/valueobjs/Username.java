package model.valueobjs;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Column;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Embeddable
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username {

    @Column(name = "username", nullable = false, unique = true)
    private String value;

    public Username(String value) {
        if (value == null || value.trim().length() < 3) {
            throw new IllegalArgumentException("Username must be atleast 3 characters");
        }
        this.value = value.trim();
    }
}
