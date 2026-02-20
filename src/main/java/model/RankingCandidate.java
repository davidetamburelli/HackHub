package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ranking_candidates")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RankingCandidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participating_team_id", nullable = false, unique = true)
    private Long eligibleParticipatingTeam;

    @Column(name = "final_score", nullable = false)
    private int finalScore;

    @Column(name = "submission_updated_at", nullable = false)
    private LocalDateTime submissionUpdatedAt;

    @Column(name = "team_registered_at", nullable = false)
    private LocalDateTime teamRegisteredAt;

    @Column(name = "team_size", nullable = false)
    private int teamSize;

    public RankingCandidate(
            Long eligibleParticipatingTeamId,
            int finalScore,
            LocalDateTime submissionUpdatedAt,
            LocalDateTime teamRegisteredAt
    ) {
        this.eligibleParticipatingTeam = eligibleParticipatingTeamId;
        this.finalScore = finalScore;
        this.submissionUpdatedAt = submissionUpdatedAt;
        this.teamRegisteredAt = teamRegisteredAt;
    }
}