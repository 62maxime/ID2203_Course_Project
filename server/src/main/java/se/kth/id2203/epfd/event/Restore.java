package se.kth.id2203.epfd.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

/**
 * Created by ralambom on 12/02/17.
 */
public class Restore implements KompicsEvent{

    private final Address source;

    public Restore(Address source) {
        this.source = source;
    }

    public Address getSource() {
        return this.source;
    }

}
