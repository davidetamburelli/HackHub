package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import model.enums.SupportRequestStatus;
import model.enums.Urgency;
import utils.DomainException;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_requests")
@Getter
@ToString(exclude = {"hackathon", "participatingTeam"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SupportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participating_team_id", nullable = false)
    private ParticipatingTeam participatingTeam;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Urgency urgency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SupportRequestStatus status;

    public SupportRequest(
            Hackathon hackathon,
            ParticipatingTeam participatingTeam,
            String title,
            String description,
            Urgency urgency,
            LocalDateTime createdAt
    ) {
        if (hackathon == null || participatingTeam == null) {
            throw new IllegalArgumentException("Hackathon e Participating Team sono obbligatori");
        }
        if (title == null || title.trim().isBlank()) {
            throw new IllegalArgumentException("Il titolo della richiesta è obbligatorio");
        }
        if (description == null || description.trim().isBlank()) {
            throw new IllegalArgumentException("La descrizione della richiesta è obbligatoria");
        }
        if (urgency == null) {
            throw new IllegalArgumentException("Il livello di urgenza è obbligatorio");
        }

        if (!participatingTeam.getHackathon().getId().equals(hackathon.getId())) {
            throw new DomainException("Il team non partecipa all'hackathon specificato");
        }

        this.hackathon = hackathon;
        this.participatingTeam = participatingTeam;
        this.title = title;
        this.description = description;
        this.urgency = urgency;

        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();

        this.status = SupportRequestStatus.OPEN;
    }

    public void assertBelongsToHackathon(Hackathon hackathon) {
        if (hackathon == null) {
            throw new IllegalArgumentException("L'hackathon di riferimento non può essere nullo");
        }
        if (!this.hackathon.getId().equals(hackathon.getId())) {
            throw new DomainException("La richiesta di supporto non appartiene all'hackathon specificato");
        }
    }
}