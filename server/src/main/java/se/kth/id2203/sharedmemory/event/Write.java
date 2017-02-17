package se.kth.id2203.sharedmemory.event;

import com.google.common.base.Optional;
import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Write implements KompicsEvent, Serializable {

    private int rid;
    private int ts;
    private int wr;
    private Optional<KVEntry> writeValue;

    public Write(int rid, int ts, int wr, Optional<KVEntry> writeValue) {
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

    public Optional<KVEntry> getWriteValue() {
        return writeValue;
    }
}
