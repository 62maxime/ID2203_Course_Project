package se.kth.id2203.multipaxos.component;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.ReplicationGroup;
import se.sics.kompics.Init;

/**
 * Created by ralambom on 23/02/17.
 */
public class MultiPaxosInit extends Init<MultiPaxos> {

    private NetAddress self;
    private ReplicationGroup replicationGroup;
    private int selfRank;

    public MultiPaxosInit(NetAddress self, ReplicationGroup replicationGroup, int selfRank) {
        this.self = self;
        this.replicationGroup = replicationGroup;
        this.selfRank = selfRank;
    }

    public NetAddress getSelf() {
        return self;
    }

    public ReplicationGroup getReplicationGroup() {
        return replicationGroup;
    }

    public int getSelfRank() {
        return selfRank;
    }
}
