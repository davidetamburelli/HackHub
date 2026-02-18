package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.enums.Urgency;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@ToString(exclude = {"hackathon", "partecipatingTeam"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "mentor_id", nullable = false)
    private StaffProfile mentor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participating_team_id", nullable = false)
    private ParticipatingTeam participatingTeam;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgency urgency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Report(Hackathon hackathon, StaffProfile mentor, ParticipatingTeam participatingTeam, String reason, Urgency urgency) {
        if (hackathon == null || mentor == null || participatingTeam == null) {
            throw new IllegalArgumentException("Hackathon, Mentor e Team sono obbligatori");
        }
        if (reason == null || reason.trim().isBlank()) {
            throw new IllegalArgumentException("Il motivo della segnalazione è obbligatorio");
        }
        if (urgency == null) {
            throw new IllegalArgumentException("L'urgenza è obbligatoria");
        }

        if (!participatingTeam.getHackathon().equals(hackathon)) {
            throw new IllegalArgumentException("Il team segnalato non appartiene all'hackathon specificato");
        }

        hackathon.assertStaff(mentor);

        this.hackathon = hackathon;
        this.mentor = mentor;
        this.participatingTeam = participatingTeam;
        this.reason = reason;
        this.urgency = urgency;
        this.createdAt = LocalDateTime.now();
    }
}