package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.valueobjs.Email;

@Entity
@Table(name = "staffProfiles")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StaffProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true))
    private Email email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String surname;

    public StaffProfile(Email email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }
}
