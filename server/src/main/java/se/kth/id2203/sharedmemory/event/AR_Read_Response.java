package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Read_Response implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 7272451227450461928L;
    private UUID uuid;
    private KVEntry value;

    public AR_Read_Response(UUID uuid, KVEntry value) {
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
