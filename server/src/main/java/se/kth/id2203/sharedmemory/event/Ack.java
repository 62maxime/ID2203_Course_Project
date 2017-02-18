package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Ack implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -1025410931340346861L;
    private UUID uuid;
    private int rid;
    private int key;

    public Ack(UUID uuid, int rid, int key) {
        this.uuid = uuid;
        this.rid = rid;
        this.key = key;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getRid() {
        return rid;
    }

    public int getKey() {
        return key;
    }
}
