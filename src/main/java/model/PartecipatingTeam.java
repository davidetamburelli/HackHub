package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "partecipatingTeams",
        uniqueConstraints = {
                // Un team non può essere iscritto 2 volte allo stesso hackathon
                @UniqueConstraint(name = "uk_part_team_hackathon", columnNames = {"team_id", "hackathon_id"})
        }
)
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PartecipatingTeam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Iscrizione di un Team a un Hackathon: tanti participatingTeam possono riferirsi allo stesso Team in hackathon diversi
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hackathon_id", nullable = false)
    private Hackathon hackathon;


    private List<User> activeMembers = new ArrayList<User>();

    @Column(name = "registered_at", nullable = false)
    private LocalDateTime registeredAt;
}
