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
    private KVEntry value;

    public AR_Write_Request(UUID uuid, KVEntry value) {
        this.uuid = uuid;
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public KVEntry getValue() {
        return value;
    }
}
