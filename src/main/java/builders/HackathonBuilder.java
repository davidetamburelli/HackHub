package builders;

import model.Hackathon;
import model.StaffProfile;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

import java.util.ArrayList;
import java.util.List;

public class HackathonBuilder implements IHackathonBuilder {

    private String name;
    private String type;
    private String location;
    private double prize;
    private int maxTeamSize;
    private String regulation;
    private StaffProfile organizer;
    private StaffProfile judge;
    private List<StaffProfile> mentors;
    private Period subscriptionDates;
    private Period dates;
    private String delivery;
    private RankingPolicy rankingPolicy;

    public HackathonBuilder() {
        this.reset();
    }

    @Override
    public void reset() {
        this.name = null;
        this.type = null;
        this.location = null;
        this.prize = 0.0;
        this.maxTeamSize = 0;
        this.regulation = null;
        this.organizer = null;
        this.judge = null;
        this.mentors = new ArrayList<>();
        this.subscriptionDates = null;
        this.dates = null;
        this.delivery = null;
        this.rankingPolicy = null;
    }

    @Override
    public IHackathonBuilder buildName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public IHackathonBuilder buildType(String type) {
        this.type = type;
        return this;
    }

    @Override
    public IHackathonBuilder buildLocation(String location) {
        this.location = location;
        return this;
    }

    @Override
    public IHackathonBuilder buildPrize(double prize) {
        this.prize = prize;
        return this;
    }

    @Override
    public IHackathonBuilder buildMaxTeamSize(int maxTeamSize) {
        this.maxTeamSize = maxTeamSize;
        return this;
    }

    @Override
    public IHackathonBuilder buildRegulation(String regulation) {
        this.regulation = regulation;
        return this;
    }

    @Override
    public IHackathonBuilder buildOrganizer(StaffProfile organizer) {
        this.organizer = organizer;
        return this;
    }

    @Override
    public IHackathonBuilder buildJudge(StaffProfile judge) {
        this.judge = judge;
        return this;
    }

    @Override
    public IHackathonBuilder buildMentors(List<StaffProfile> mentors) {
        this.mentors = mentors != null ? mentors : new ArrayList<>();
        return this;
    }

    @Override
    public IHackathonBuilder buildSubscriptionDates(Period subscriptionDates) {
        this.subscriptionDates = subscriptionDates;
        return this;
    }

    @Override
    public IHackathonBuilder buildDates(Period dates) {
        this.dates = dates;
        return this;
    }

    @Override
    public IHackathonBuilder buildDelivery(String delivery) {
        this.delivery = delivery;
        return this;
    }

    @Override
    public IHackathonBuilder buildRankingPolicy(RankingPolicy rankingPolicy) {
        this.rankingPolicy = rankingPolicy;
        return this;
    }

    @Override
    public Hackathon build() {

        Hackathon hackathon = new Hackathon(
                name,
                type,
                prize,
                maxTeamSize,
                regulation,
                organizer,
                judge,
                mentors,
                delivery,
                location,
                rankingPolicy,
                subscriptionDates,
                dates
        );

        this.reset();

        return hackathon;
    }
}