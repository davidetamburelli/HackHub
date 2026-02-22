package utils.decorator;

import model.Hackathon;
import model.dto.responsedto.PublicHackathonViewDTO;

public final class WinnerInfoDecorator extends PublicHackathonViewDecorator {

    private final Hackathon h;

    public WinnerInfoDecorator(PublicHackathonView inner, Hackathon h) {
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
                base.delivery(),
                h.getWinnerParticipatingTeamId()
        );
    }
}
