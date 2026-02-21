package model.dto.responsedto;

import model.enums.HackathonStatus;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

import java.util.List;

public record HackathonFullDetailsDTO(
        Long id,
        String name,
        String type,
        String location,
        double prize,
        int maxTeamSize,
        String regulation,
        StaffSummaryDTO organizer,
        StaffSummaryDTO judge,
        List<StaffSummaryDTO> mentors,
        Period subscriptionDates,
        Period dates,
        String delivery,
        RankingPolicy rankingPolicy,
        HackathonStatus status,
        Long winnerParticipatingTeamId,
        PrizePayoutDTO prizePayout
) {}
