package utils.decorator;

public abstract class PublicHackathonViewDecorator implements PublicHackathonView{
    protected final PublicHackathonView inner;

    protected PublicHackathonViewDecorator(PublicHackathonView inner) {
        this.inner = inner;
    }
}
