package se.kth.id2203.multipaxos.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

/**
 * Created by ralambom on 23/02/17.
 */
public class MultiPaxosInit extends Init<MultiPaxos> {

    private NetAddress self;
    private int selfRank;

    public MultiPaxosInit(NetAddress self, int selfRank) {
        this.self = self;
        this.selfRank = selfRank;
    }

    public NetAddress getSelf() {
        return self;
    }

    public int getSelfRank() {
        return selfRank;
    }
}
