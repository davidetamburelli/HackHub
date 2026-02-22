package utils.decorator;

import model.Hackathon;
import model.dto.responsedto.PublicHackathonViewDTO;

public final class EvaluationInfoDecorator extends PublicHackathonViewDecorator {

    private final Hackathon h;

    public EvaluationInfoDecorator(PublicHackathonView inner, Hackathon h) {
        super(inner);
        this.h = h;
    }

    @Override
    public PublicHackathonViewDTO toDto() {
        PublicHackathonViewDTO base = inner.toDto();
        return new PublicHackathonViewDTO(
                base.id(), base.name(), base.type(), base.location(), base.prize(),
                base.organizer(),
                base.subscriptionDates(), base.dates(), base.status(),
                base.maxTeamSize(), base.regulation(), base.rankingPolicy(),
                h.getDelivery(),
                base.winnerParticipatingTeamId()
        );
    }
}