package se.kth.id2203.sharedmemory.component;

import se.kth.id2203.kvstore.KVEntry;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class RIWCInit extends Init<RIWC> {

    private NetAddress self;
    private int n;
    private int selfRank;
    private HashMap<Integer, KVEntry> store;

    public RIWCInit(NetAddress self, int n, int selfRank, HashMap<Integer, KVEntry> store) {
        this.self = self;
        this.n = n;
        this.selfRank = selfRank;
        this.store = store;
    }

    public NetAddress getSelf() {
        return self;
    }

    public int getN() {
        return n;
    }

    public int getSelfRank() {
        return selfRank;
    }

    public HashMap<Integer, KVEntry> getStore() {
        return store;
    }
}
