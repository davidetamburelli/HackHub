package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.enums.InvitationStatus;
import utils.DomainException;

@Entity
@Table(name = "invitations")
@Getter
@ToString(exclude = {"team"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "invitee_id", nullable = false)
    private User invitee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InvitationStatus status;

    public Invitation(Team team, User invitee) {
        if (team == null || invitee == null) {
            throw new IllegalArgumentException("Team e Utente invitato sono obbligatori");
        }
        this.team = team;
        this.invitee = invitee;
        this.status = InvitationStatus.PENDING;
    }

    public void assertInvitee(User user) {
        if (user == null || !this.invitee.equals(user)) {
            throw new DomainException("Operazione non autorizzata: l'invito non è rivolto a questo utente");
        }
    }

    public void assertPending() {
        if (this.status != InvitationStatus.PENDING) {
            throw new DomainException("L'invito non è più in attesa (Stato attuale: " + this.status + ")");
        }
    }

    public void accept() {
        assertPending();
        this.status = InvitationStatus.ACCEPTED;
    }

    public void reject() {
        assertPending();
        this.status = InvitationStatus.REJECTED;
    }
}