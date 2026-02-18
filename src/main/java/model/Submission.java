package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import utils.DomainException;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@ToString(exclude = {"hackathon", "participatingTeams", "evaluation"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "hackathon_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_submission_hackathon")
    )
    private Hackathon hackathon;

    // --------- RELAZIONE CON PARTICIPATING TEAM ---------
    // Una submission per team partecipante
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participating_team_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_submission_participating_team")
    )
    private ParticipatingTeam participatingTeam;
    @Column(name = "response_text", nullable = false)
    private String responseText;

    @Column(name = "response_url", nullable = false, length = 500)
    private String responseUrl;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Evaluation evaluation;

    public Submission(Hackathon hackathon, ParticipatingTeam team, String responseText, String responseUrl) {
        if (hackathon == null || team == null) {
            throw new IllegalArgumentException("Hackathon e Team sono obbligatori");
        }
        this.hackathon = hackathon;
        this.participatingTeam = team;
        this.responseText = responseText;
        this.responseUrl = responseUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public Evaluation addEvaluation(Long judgeId, int score, String comment) {
        if (this.evaluation != null) {
            throw new IllegalStateException("Submission già valutata");
        }
        this.evaluation = new Evaluation(this, score, comment);
        return this.evaluation;
    }

    public void assertBelongsToHackathon(Hackathon hackathon) {
        if (this.hackathon != hackathon) {
            throw new DomainException("La sottomissione non appartiene all'hackathon selezionato");
        }
    }

    public int finalScore() {
        if (evaluation == null)
            throw new IllegalStateException("Non ancora valutata");
        return evaluation.getScore();
    }

}
