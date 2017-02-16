package se.kth.id2203.beb.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.util.Set;

/**
 * Created by YannL on 16/02/2017.
 */
public class BebTopology implements KompicsEvent {
    private final Set<NetAddress> topology;

    public BebTopology(Set<NetAddress> topology) {
        this.topology = topology;
    }

    public Set<NetAddress> getTopology() {
        return topology;
    }
}
