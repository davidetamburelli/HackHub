package model.mappers;

import model.Hackathon;
import model.ParticipatingTeam;
import model.StaffProfile;
import model.Team;
import model.dto.responsedto.*;
import model.valueobjs.PrizePayout;

import java.util.List;

public final class HackathonDTOMapper {
    private HackathonDTOMapper() {}

    public static HackathonSummaryDTO toSummary(Hackathon h) {
        return new HackathonSummaryDTO(
                h.getId(),
                h.getName(),
                h.getType(),
                h.getDates().getStartDate(),
                h.getDates().getEndDate(),
                h.getStatus()
        );
    }

    public static HackathonFullDetailsDTO toFullDetails(Hackathon h, StaffProfile organizer, StaffProfile judge, List<StaffProfile> mentors) {
        return new HackathonFullDetailsDTO(
                h.getId(),
                h.getName(),
                h.getType(),
                h.getLocation(),
                h.getPrize(),
                h.getMaxTeamSize(),
                h.getRegulation(),
                toStaffSummary(organizer),
                toStaffSummary(judge),
                mentors.stream()
                        .map(HackathonDTOMapper::toStaffSummary)
                        .toList(),
                h.getSubscriptionDates(),
                h.getDates(),
                h.getDelivery(),
                h.getRankingPolicy(),
                h.getStatus(),
                h.getWinnerParticipatingTeamId(),
                toPrizePayoutDTO(h.getPrizePayout())
        );
    }

    public static DeclareWinnerResponseDTO toDeclareWinnerResponse(Hackathon h, ParticipatingTeam pt) {
        return new DeclareWinnerResponseDTO(
                h.getId(),
                h.getStatus(),
                toWinner(pt)
        );
    }

    public static PrizePayoutResponseDTO toPrizePayoutResponse(Hackathon h) {
        return new PrizePayoutResponseDTO(
                h.getId(),
                h.getPrizePayout().getStatus(),
                h.getPrizePayout().getPaidAt(),
                h.getPrizePayout().getProviderRef(),
                h.getPrizePayout().getFailureReason()
        );
    }

    private static StaffSummaryDTO toStaffSummary(StaffProfile staff) {
        if (staff == null) return null;

        return new StaffSummaryDTO(
                staff.getId(),
                staff.getEmail(),
                staff.getName(),
                staff.getSurname()
        );
    }

    private static PrizePayoutDTO toPrizePayoutDTO(PrizePayout payout) {
        if (payout == null) return null;

        return new PrizePayoutDTO(
                payout.getStatus(),
                payout.getPaidAt(),
                payout.getProviderRef(),
                payout.getFailureReason()
        );
    }


    private static WinnerParticipatingTeamDTO toWinner(ParticipatingTeam pt) {
        return new WinnerParticipatingTeamDTO(
                pt.getId(),
                pt.getTeam(),
                pt.getContactEmail(),
                pt.getRegisteredAt(),
                pt.getTotalPenaltyPoints()
        );
    }

}
