package se.kth.id2203.common.port;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by 62maxime on 23/02/2017.
 */
public class GroupTopology implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -8324127163480102743L;
    private Set<NetAddress> topology;

    public GroupTopology(Set<NetAddress> topology) {
        this.topology = topology;
    }

    public Set<NetAddress> getTopology() {
        return topology;
    }


}
