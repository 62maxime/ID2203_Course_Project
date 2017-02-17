package se.kth.id2203.sharedmemory.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class RIWCInit extends Init<RIWC> {

    private NetAddress self;
    private int selfRank;

    public RIWCInit(NetAddress self, int selfRank) {
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
