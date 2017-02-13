package se.kth.id2203.epfd.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by ralambom on 13/02/17.
 */
public class ListenTo implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 8874431379881971989L;

    private final Set<NetAddress> addresses;

    public ListenTo(Set<NetAddress> addresses) {
        this.addresses = addresses;
    }

    public Set<NetAddress> getAddresses() {
        return this.addresses;
    }
}
