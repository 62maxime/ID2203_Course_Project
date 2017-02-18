package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Write_Request implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 3210761070543736747L;
    private UUID uuid;
    private int key;
    private KVEntry value;

    public AR_Write_Request(UUID uuid, int key, KVEntry value) {
        this.uuid = uuid;
        this.key = key;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKey() {
        return key;
    }

    public KVEntry getValue() {
        return value;
    }
}
