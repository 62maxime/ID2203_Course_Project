package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Value implements KompicsEvent, Serializable {

    private int rid;
    private int ts;
    private int wr;
    private KVEntry value;

    public Value(int rid, int ts, int wr, KVEntry value) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.value = value;
    }

    public int getRid() {
        return rid;
    }

    public int getTs() {
        return ts;
    }

    public int getWr() {
        return wr;
    }

    public KVEntry getValue() {
        return value;
    }
}
