package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by 62maxime on 18/02/2017.
 */
public class Topology implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 2613137294885407248L;
    private Set<NetAddress> netAddresses;

    public Topology(Set<NetAddress> netAddresses) {
        this.netAddresses = netAddresses;
    }

    public Set<NetAddress> getNetAddresses() {
        return netAddresses;
    }
}
