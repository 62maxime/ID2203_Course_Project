package se.kth.id2203.common.port;

import se.kth.id2203.overlay.ReplicationGroup;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 23/02/2017.
 */
public class GroupTopology implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -8324127163480102743L;
    private ReplicationGroup topology;

    public GroupTopology(ReplicationGroup topology) {
        this.topology = topology;
    }

    public ReplicationGroup getTopology() {
        return topology;
    }


}
