package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Write_Response implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -7501405725244086244L;
    private UUID uuid;
    private int key;

    public AR_Write_Response(UUID uuid, int key) {
        this.uuid = uuid;
        this.key = key;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getKey() {
        return key;
    }
}
