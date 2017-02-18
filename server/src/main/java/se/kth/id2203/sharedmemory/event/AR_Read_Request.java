package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Read_Request implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 2409338200665103565L;
    private UUID uuid;
    private int key;

    public AR_Read_Request(UUID uuid, int key) {
        this.uuid = uuid;
        this.key = key;
    }

    public int getKey() {
        return key;
    }

    public UUID getUuid() {
        return uuid;
    }
}
