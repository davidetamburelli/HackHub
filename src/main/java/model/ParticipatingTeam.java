package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "participatingTeams",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_part_team_hackathon", columnNames = {"team_id", "hackathon_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipatingTeam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "team_id", nullable = false)
    private Long team;

    @Column(name = "hackathon_id", nullable = false)
    private Long hackathon;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "pt_active_members",
            joinColumns = @JoinColumn(name = "participating_team_id")
    )
    @Column(name = "user_id", nullable = false)
    private List<Long> activeMembers = new ArrayList<>();

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    public ParticipatingTeam(Long hackathonId, Long teamId, List<Long> activeMembersId) {
        this.hackathon = hackathonId;
        this.team = teamId;
        this.activeMembers = new ArrayList<>(activeMembersId);
        this.registeredAt = LocalDateTime.now();
    }
}