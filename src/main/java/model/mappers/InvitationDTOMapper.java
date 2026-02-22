package model.mappers;

import model.Invitation;
import model.Team;
import model.dto.responsedto.InvitationDetailsDTO;
import model.dto.responsedto.InvitationSummaryDTO;
import model.dto.responsedto.TeamRefDTO;

public final class InvitationDTOMapper {

    private InvitationDTOMapper() {}

    public static InvitationSummaryDTO toSummary(Invitation i, String teamName) {
        return new InvitationSummaryDTO(
                i.getId(),
                i.getTeamId(),
                teamName,
                i.getStatus()
        );
    }

    public static InvitationSummaryDTO toSummary(Invitation i, Team team) {
        return new InvitationSummaryDTO(
                i.getId(),
                i.getTeamId(),
                team != null ? team.getName() : null,
                i.getStatus()
        );
    }

    // =========================
    // DETAILS
    // =========================

    public static InvitationDetailsDTO toDetails(Invitation i, Team team) {
        return new InvitationDetailsDTO(
                i.getId(),
                i.getStatus(),
                toTeamRefDTO(team)
        );
    }

    // =========================
    // MAPPER SECONDARIO
    // =========================

    private static TeamRefDTO toTeamRefDTO(Team team) {
        if (team == null) return null;

        return new TeamRefDTO(
                team.getId(),
                team.getName(),
                team.getLeader()
        );
    }
}