package model.dto.responsedto;

import model.enums.HackathonStatus;
import model.enums.RankingPolicy;
import model.valueobjs.Period;

public record PublicHackathonViewDTO(
        long id,
        String name,
        String type,
        String location,
        double prize,
        StaffPublicDTO organizer,
        Period subscriptionDates,
        Period dates,
        HackathonStatus status,

        // IN_REGISTRATION + READY_TO_START + RUNNING (+ in_evaluation/closed)
        Integer maxTeamSize,
        String regulation,
        RankingPolicy rankingPolicy,

        // IN_EVALUATION (+ closed)
        String delivery,

        // CLOSED + winner != null
        Long winnerParticipatingTeamId
) {}