package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Value implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -4098152603293776232L;
    private UUID uuid;
    private int rid;
    private int ts;
    private int wr;
    private int key;
    private KVEntry value;

    public Value(UUID uuid, int rid, int ts, int wr, int key, KVEntry value) {
        this.uuid = uuid;
        this.rid = rid;
        this.ts = ts;
        this.wr = wr;
        this.key = key;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
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

    public int getKey() {
        return key;
    }

    public KVEntry getValue() {
        return value;
    }
}
