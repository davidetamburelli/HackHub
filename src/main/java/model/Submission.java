package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
@Getter
@ToString(exclude = {"hackathon", "participatingTeam", "evaluation"})
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

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "participating_team_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_submission_participating_team")
    )
    private ParticipatingTeam participatingTeam;

    @Column(name = "response", nullable = false)
    private String response;

    @Column(name = "response_url", nullable = false, length = 500)
    private String responseURL;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "submission", cascade = CascadeType.ALL, orphanRemoval = true)
    private Evaluation evaluation;

    public Submission(Hackathon hackathon, ParticipatingTeam participatingTeam, String response, String responseURL) {
        if (hackathon == null || participatingTeam == null) {
            throw new IllegalArgumentException("Hackathon e ParticipatingTeam sono obbligatori");
        }
        this.hackathon = hackathon;
        this.participatingTeam = participatingTeam;
        this.response = response;
        this.responseURL = responseURL;
        this.updatedAt = LocalDateTime.now();
    }

    public Evaluation addEvaluation(int score, String comment) {
        if (this.hasEvaluation()) {
            throw new IllegalStateException("Submission già valutata");
        }

        this.evaluation = new Evaluation(score, comment);

        this.evaluation.assignSubmission(this);

        return this.evaluation;
    }

    public boolean hasEvaluation() {
        return this.evaluation != null;
    }
}