package se.kth.id2203.leaderdetection.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

import java.util.Set;

/**
 * Created by 62maxime on 22/02/2017.
 */
public class MELDInit extends Init<MELD> {

    private Set<NetAddress> topology;

    public MELDInit(Set<NetAddress> topology) {
        this.topology = topology;
    }

    public Set<NetAddress> getTopology() {
        return topology;
    }
}
