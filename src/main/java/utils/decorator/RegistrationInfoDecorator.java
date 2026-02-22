package utils.decorator;

import model.Hackathon;
import model.dto.responsedto.PublicHackathonViewDTO;

public final class RegistrationInfoDecorator extends PublicHackathonViewDecorator{
    private final Hackathon h;

    public RegistrationInfoDecorator(PublicHackathonView inner, Hackathon h) {
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
                h.getMaxTeamSize(),
                h.getRegulation(),
                h.getRankingPolicy(),
                base.delivery(),
                base.winnerParticipatingTeamId()
        );
    }
}
