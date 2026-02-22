package model.mappers;

import model.Hackathon;
import model.StaffProfile;
import model.dto.responsedto.HackathonFullDetailsDTO;
import model.dto.responsedto.HackathonSummaryDTO;
import model.dto.responsedto.PrizePayoutDTO;
import model.dto.responsedto.StaffSummaryDTO;
import model.valueobjs.PrizePayout;

import java.util.List;

public final class HackathonDTOMapper {
    private HackathonDTOMapper() {}

    public static HackathonSummaryDTO toSummary(Hackathon h) {
        return new HackathonSummaryDTO(
                h.getId(),
                h.getName(),
                h.getType(),
                h.getDates().getStartDate(),   // supponendo Period abbia getStart()
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

    private static StaffSummaryDTO toStaffSummary(StaffProfile staff) {
        if (staff == null) return null;

        return new StaffSummaryDTO(
                staff.getId(),
                staff.getEmail().toString(),
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




}
