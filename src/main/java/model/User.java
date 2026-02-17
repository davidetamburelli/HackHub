package model;

import model.valueobjs.Email;
import model.valueobjs.Password;
import model.valueobjs.Username;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "users")
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "Username", nullable = false, unique = true))
    private Username username;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password"))
    private Password password;

    @Column(name = "team_id", nullable = true)
    @Setter
    private Long teamId;

    public User(Username username, Email email, Password password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.teamId = null;
    }

    public void assignTeam(Long teamId) {
        this.teamId = teamId;
    }

    public void leaveTeam() {
        this.teamId = null;
    }
}
