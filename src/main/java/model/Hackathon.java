package model;

import jakarta.persistence.*;
import lombok.*;
import model.enums.HackathonStatus;
import model.enums.RankingPolicy;
import model.valueobjs.Period;
import utils.DomainException;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hackathons")
@Getter
@ToString(exclude = {"organizer", "judge", "mentors", "participatingTeams"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Hackathon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 40)
    private String type;

    @Column(length = 150)
    private String location;

    @Column(length = 100)
    private String delivery;

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
    private Period dates; // Rinominato da hackathonDates

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private StaffProfile organizer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "judge_id", nullable = false)
    private StaffProfile judge;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "hackathon_mentors",
            joinColumns = @JoinColumn(name = "hackathon_id"),
            inverseJoinColumns = @JoinColumn(name = "mentor_id")
    )
    private List<StaffProfile> mentors = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HackathonStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private RankingPolicy rankingPolicy;

    @OneToMany(mappedBy = "hackathon", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParticipatingTeam> participatingTeams = new ArrayList<>();

    public Hackathon(
            String name,
            String type,
            double prize,
            int maxTeamSize,
            String regulation,
            StaffProfile organizer,
            StaffProfile judge,
            List<StaffProfile> mentors,
            String delivery,
            String location,
            RankingPolicy rankingPolicy,
            Period subscriptionDates,
            Period dates
    ) {
        this.name = name;
        this.type = type;
        this.prize = prize;
        this.maxTeamSize = maxTeamSize;
        this.regulation = regulation;
        this.organizer = organizer;
        this.judge = judge;
        this.mentors = mentors != null ? mentors : new ArrayList<>();
        this.delivery = delivery;
        this.location = location;
        this.rankingPolicy = rankingPolicy;
        this.subscriptionDates = subscriptionDates;
        this.dates = dates;

        this.status = HackathonStatus.CREATED;
    }

    public void addParticipatingTeam(ParticipatingTeam pt) {
        if (pt != null) {
            this.participatingTeams.add(pt);
        }
    }

    public void assertInRegistration() {
        if (!status.equals(HackathonStatus.IN_REGISTRATION)) {
            throw new DomainException("Operazione non autorizzata: l'hackathon non è in fase di registrazione");
        }
    }

    public void assertRunning() {
        if (!status.equals(HackathonStatus.RUNNING)) {
            throw new DomainException("Operazione non autorizzata: l'hackathon non è in fase di svolgimento");
        }
    }

    public void assertInEvaluation() {
        if (!status.equals(HackathonStatus.IN_EVALUATION)) {
            throw new DomainException("Operazione non autorizzata: l'hackathon non è in fase di valutazione");
        }
    }

    public void assertStaff(StaffProfile staff) {
        if (staff == null) throw new IllegalArgumentException("Staff profile nullo");

        boolean isOrganizer = staff.equals(this.organizer);
        boolean isJudge = staff.equals(this.judge);
        boolean isMentor = this.mentors.contains(staff);

        if (!isOrganizer && !isJudge && !isMentor) {
            throw new DomainException("L'utente non fa parte dello staff di questo hackathon");
        }
    }

    public void assertJudge(StaffProfile staff) {
        if (staff == null || !staff.equals(this.judge)) {
            throw new DomainException("Staff profile non autorizzato: non è il giudice dell'hackathon");
        }
    }

    public void assertMentor(StaffProfile staff) {
        if (staff == null || !this.mentors.contains(staff)) {
            throw new DomainException("Staff profile non autorizzato: non è un mentor dell'hackathon");
        }
    }

    public void assertTeamSizeAllowed(int teamSize) {
        if(teamSize <= 0 || teamSize > maxTeamSize) {
            throw new DomainException("Iscrizione non autorizzata: il numero di membri del team non è valido (" + teamSize + "/" + maxTeamSize + ")");
        }
    }
}