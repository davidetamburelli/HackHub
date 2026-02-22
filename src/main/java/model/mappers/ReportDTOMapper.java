package model.mappers;

import model.ParticipatingTeam;
import model.Report;
import model.StaffProfile;
import model.dto.responsedto.ParticipatingTeamRefDTO;
import model.dto.responsedto.ReportDetailsDTO;
import model.dto.responsedto.ReportSummaryDTO;
import model.dto.responsedto.StaffRefDTO;

public final class ReportDTOMapper {

    private ReportDTOMapper() {}

    public static ReportSummaryDTO toSummary(Report r) {

        return new ReportSummaryDTO(
                r.getId(),
                r.getHackathon(),
                r.getParticipatingTeam(),
                r.getMentor(),
                r.getUrgency(),
                r.getCreatedAt(),
                r.isResolved(),
                r.isResolved() ? r.getReportResolution() : null
        );
    }

    public static ReportDetailsDTO toDetails(
            Report r,
            ParticipatingTeam team,
            StaffProfile mentor
    ) {

        return new ReportDetailsDTO(
                r.getId(),
                r.getHackathon(),
                toParticipatingTeamRefDTO(team),
                toStaffRefDTO(mentor),
                r.getReason(),
                r.getUrgency(),
                r.getCreatedAt(),
                r.isResolved(),
                r.isResolved() ? r.getReportResolution() : null
        );
    }

    private static ParticipatingTeamRefDTO toParticipatingTeamRefDTO(
            ParticipatingTeam team
    ) {
        if (team == null) return null;

        return new ParticipatingTeamRefDTO(
                team.getId(),
                team.getTeam(),
                team.getContactEmail(),
                team.isDisqualified(),
                team.getTotalPenaltyPoints()
        );
    }

    private static StaffRefDTO toStaffRefDTO(StaffProfile staff) {
        if (staff == null) return null;

        return new StaffRefDTO(
                staff.getId(),
                staff.getEmail().toString(),
                staff.getName(),
                staff.getSurname()
        );
    }
}