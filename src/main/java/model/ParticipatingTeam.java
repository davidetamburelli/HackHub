package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import utils.DomainException;

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
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ParticipatingTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "pt_active_members",
            joinColumns = @JoinColumn(name = "participating_team_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(
                    name = "uk_pt_active_member",
                    columnNames = {"participating_team_id", "user_id"}
            )
    )
    private List<User> activeMembers = new ArrayList<User>();

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;

    private boolean isActiveMember(Long userId) {
        return userId != null && activeMembers.stream().anyMatch(u -> u.getId().equals(userId));
    }

    public ParticipatingTeam(Hackathon hackathon, Team team) {
        if (hackathon == null || team == null) {
            throw new IllegalArgumentException("Hackathon e Team sono obbligatori");
        }
        this.hackathon = hackathon;
        this.team = team;
        this.registeredAt = LocalDateTime.now();
    }

    public void assertActiveMember(Long userId) {
        if (!isActiveMember(userId)) {
            throw new DomainException("Utente non autorizzato: non è tra gli activeMembers");
        }
    }

}
