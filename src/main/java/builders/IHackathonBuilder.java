package builders;

import model.Hackathon;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

import java.util.List;

public interface IHackathonBuilder {
    void reset();

    IHackathonBuilder buildName(String name);
    IHackathonBuilder buildType(String type);
    IHackathonBuilder buildLocation(String location);
    IHackathonBuilder buildPrize(double prize);
    IHackathonBuilder buildMaxTeamSize(int maxTeamSize);
    IHackathonBuilder buildRegulation(String regulation);
    IHackathonBuilder buildOrganizer(Long organizerId);
    IHackathonBuilder buildJudge(Long judgeId);
    IHackathonBuilder buildMentors(List<Long> mentorsId);
    IHackathonBuilder buildSubscriptionDates(Period subscriptionDates);
    IHackathonBuilder buildDates(Period dates);
    IHackathonBuilder buildDelivery(String delivery);
    IHackathonBuilder buildRankingPolicy(RankingPolicy rankingPolicy);

    Hackathon build();
}