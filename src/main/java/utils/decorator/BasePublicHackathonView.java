package utils.decorator;

import model.Hackathon;
import model.StaffProfile;
import model.dto.responsedto.PublicHackathonViewDTO;
import model.dto.responsedto.StaffPublicDTO;
import model.valueobjs.Period;

public final class BasePublicHackathonView implements PublicHackathonView {

    private final Hackathon h;
    private final StaffProfile organizer;

    public BasePublicHackathonView(Hackathon h, StaffProfile organizer) {
        this.h = h;
        this.organizer = organizer;
    }

    @Override
    public PublicHackathonViewDTO toDto() {
        return new PublicHackathonViewDTO(
                h.getId(),
                h.getName(),
                h.getType(),
                h.getLocation(),
                h.getPrize(),
                new StaffPublicDTO(organizer.getId(), organizer.getName(), organizer.getSurname()),
                new Period(h.getSubscriptionDates().getStartDate(), h.getSubscriptionDates().getEndDate()),
                new Period(h.getDates().getStartDate(), h.getDates().getEndDate()),
                h.getStatus(),
                null, null, null,
                null,
                null
        );
    }
}
