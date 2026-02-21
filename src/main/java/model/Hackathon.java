package model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import model.enums.HackathonStatus;
import model.enums.PrizeStatus;
import model.enums.RankingPolicy;
import model.valueobjs.Period;
import model.valueobjs.PrizePayout;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hackathons")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hackathon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @Column
    private String location;

    @Column(nullable = false)
    private double prize;

    @Column(name = "max_team_size", nullable = false)
    private int maxTeamSize;

    @Column(nullable = false, length = 2000)
    private String regulation;

    @Column(name = "organizer_id", nullable = false)
    private Long organizer;

    @Column(name = "judge_id", nullable = false)
    private Long judge;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "hackathon_mentors",
            joinColumns = @JoinColumn(name = "hackathon_id")
    )
    @Column(name = "staff_profile_id", nullable = false)
    private List<Long> mentors = new ArrayList<>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "sub_start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "sub_end_date"))
    })
    private Period subscriptionDates;

    @Column(nullable = false)
    private String delivery;

    @Enumerated(EnumType.STRING)
    @Column(name = "ranking_policy", nullable = false)
    private RankingPolicy rankingPolicy;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "startDate", column = @Column(name = "start_date")),
            @AttributeOverride(name = "endDate", column = @Column(name = "end_date"))
    })
    private Period dates;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HackathonStatus status;

    @Column(name = "winner_participating_team_id")
    private Long winnerParticipatingTeam;

    @Embedded
    private PrizePayout prizePayout;

    public Hackathon(
            String name,
            String type,
            String location,
            double prize,
            int maxTeamSize,
            String regulation,
            Long organizerId,
            Long judgeId,
            List<Long> mentorsId,
            String delivery,
            RankingPolicy rankingPolicy,
            Period subscriptionDates,
            Period dates
    ) {
        this.name = name;
        this.type = type;
        this.location = location;
        this.prize = prize;
        this.maxTeamSize = maxTeamSize;
        this.regulation = regulation;
        this.organizer = organizerId;
        this.judge = judgeId;
        this.mentors = mentorsId != null ? new ArrayList<>(mentorsId) : new ArrayList<>();
        this.delivery = delivery;
        this.rankingPolicy = rankingPolicy;
        this.subscriptionDates = subscriptionDates;
        this.dates = dates;
    }

    public void close() {
        this.status = HackathonStatus.CLOSED;
    }

    public void declareWinner(Long winnerParticipatingTeamId) {
        this.winnerParticipatingTeam = winnerParticipatingTeamId;
        this.prizePayout = new PrizePayout(PrizeStatus.NOT_PAID, null, null, null);
    }

    public Long getWinnerParticipatingTeamId() {
        return this.winnerParticipatingTeam;
    }

    public PrizeStatus getPrizeStatus() {
        return this.prizePayout != null ? this.prizePayout.getStatus() : null;
    }

    public void confirmPrizePaid(String providerRef, LocalDateTime paidAt) {
        this.prizePayout = new PrizePayout(PrizeStatus.PAID, paidAt, providerRef, null);
    }

    public void markPrizeFailed(String failureReason, LocalDateTime paidAt) {
        this.prizePayout = new PrizePayout(PrizeStatus.FAILED, paidAt, null, failureReason);
    }

    public void closeEvaluations() {
        this.status = HackathonStatus.CLOSED;
    }
}