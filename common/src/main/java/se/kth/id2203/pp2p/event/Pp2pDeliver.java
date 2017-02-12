package se.kth.id2203.pp2p.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

import java.io.Serializable;

/**
 * Created by ralambom on 11/02/17.
 */
public abstract class Pp2pDeliver implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -314262496401376431L;
    private Address source;

    public Pp2pDeliver(Address source) {
        this.source = source;
    }

    public Address getSource() {
        return source;
    }
}
