package model.mappers;

import model.ParticipatingTeam;
import model.SupportRequest;
import model.dto.responsedto.ParticipatingTeamRefDTO;
import model.dto.responsedto.SupportRequestDetailsDTO;
import model.dto.responsedto.SupportRequestSummaryDTO;

public final class SupportRequestDTOMapper {

    private SupportRequestDTOMapper() {}

    public static SupportRequestSummaryDTO toSummary(SupportRequest r) {
        return new SupportRequestSummaryDTO(
                r.getId(),
                r.getHackathon(),
                r.getParticipatingTeam(),
                r.getTitle(),
                r.getStatus(),
                r.getUrgency()
        );
    }

    public static SupportRequestDetailsDTO toDetails(
            SupportRequest r,
            ParticipatingTeam requesterTeam
    ) {
        return new SupportRequestDetailsDTO(
                r.getId(),
                r.getHackathon(),
                toParticipatingTeamRefDTO(requesterTeam),
                r.getStatus(),
                r.getUrgency(),
                r.getTitle(),
                r.getDescription(),
                r.getCreatedAt()
        );
    }


    private static ParticipatingTeamRefDTO toParticipatingTeamRefDTO(ParticipatingTeam team) {
        if (team == null) return null;

        return new ParticipatingTeamRefDTO(
                team.getId(),
                team.getTeam(),
                team.getContactEmail(),
                team.isDisqualified(),
                team.getTotalPenaltyPoints()
        );
    }
}