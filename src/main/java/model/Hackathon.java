package model;

import jakarta.persistence.*;
import lombok.*;
import model.enums.HackathonStatus;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hackathons")
@Getter
@ToString(exclude = {"organizer", "judge", "mentors", "partecipatingTeams"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(nullable = false)
    private double prize;

    @Column(nullable = false)
    private int maxTeamSize;

    @Lob
    @Column(nullable = false)
    private String regulation;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "subscription_start_at", nullable = false)),
            @AttributeOverride(name = "endAt", column = @Column(name = "subscription_end_at", nullable = false))
    })
    private Period subscriptionDates;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startAt", column = @Column(name = "hackathon_start_at", nullable = false)),
            @AttributeOverride(name = "endAt", column = @Column(name = "hackathon_end_at", nullable = false))
    })
    private Period hackathonDates;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private StaffProfile organizer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "judge_id", nullable = false)
    private StaffProfile judge;

    @ManyToMany(fetch = FetchType.LAZY)
    private List<StaffProfile> mentors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HackathonStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private RankingPolicy rankingPolicy;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PartecipatingTeam> partecipatingTeams = new ArrayList<>();

    public Hackathon(
            String name,
            String type,
            double prize,
            int maxTeamSize,
            String regulation,
            Period subscriptionDates,
            Period hackathonDates,
            StaffProfile organizer,
            StaffProfile judge,
            RankingPolicy rankingPolicy
    ) {
        this.name = name;
        this.type = type;
        this.prize = prize;
        this.maxTeamSize = maxTeamSize;
        this.regulation = regulation;
        this.subscriptionDates = subscriptionDates;
        this.hackathonDates = hackathonDates;
        this.organizer = organizer;
        this.judge = judge;
        this.rankingPolicy = rankingPolicy;

        // default sensato (poi puoi gestirlo con State pattern)
        this.status = HackathonStatus.CREATED;
    }

    public void addParticipatingTeam(PartecipatingTeam pt) {
        partecipatingTeams.add(pt);
    }

}
