package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.enums.Urgency;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @Column(name = "mentor_id", nullable = false)
    private Long mentor;

    @Column(name = "participating_team_id", nullable = false)
    private Long partecipatingTeam;

    @Column(nullable = false, length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Urgency urgency;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public Report(
            Long hackathonId,
            Long mentorId,
            Long participatingTeamId,
            String reason,
            Urgency urgency,
            LocalDateTime createdAt
    ) {
        this.hackathon = hackathonId;
        this.mentor = mentorId;
        this.partecipatingTeam = participatingTeamId;
        this.reason = reason;
        this.urgency = urgency;
        this.createdAt = createdAt;
    }
}