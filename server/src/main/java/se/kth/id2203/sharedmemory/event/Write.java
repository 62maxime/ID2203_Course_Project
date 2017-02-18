package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Write implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 7144606390977303585L;
    private int rid;
    private int ts;
    private int wr;
    private KVEntry writeValue;

    public Write(int rid, int ts, int wr, KVEntry writeValue) {
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.writeValue = writeValue;
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

    public KVEntry getWriteValue() {
        return writeValue;
    }
}
