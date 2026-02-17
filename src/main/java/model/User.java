package model;

import model.valueobjs.Email;
import model.valueobjs.Password;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true))
    private Email email;

    private String username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password"))
    private Password password;

    private Long teamId;

    public User(String email, String password) {
        this.email = new Email(email);
        this.password = new Password(password);
    }

    protected User() {}
}
