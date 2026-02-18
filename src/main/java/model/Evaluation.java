package model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.*;

@Entity
@Table(name = "evaluations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;
    private int score;
    private String comment;

    public Evaluation(
            Submission submission,
            int score,
            String comment
    ) {
        if (score < 0 || score > 10)
            throw new IllegalArgumentException("Score deve essere 0..10");
        this.submission = submission;
        this.score = score;
        this.comment = comment;
    }
}
