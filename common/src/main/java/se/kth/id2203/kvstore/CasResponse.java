package se.kth.id2203.kvstore;

import se.sics.kompics.KompicsEvent;

import java.util.UUID;

/**
 * Created by 62maxime on 23/02/2017.
 */
public class CasResponse extends OpResponse implements KompicsEvent {

    private KVEntry value;

    public CasResponse(UUID id, Code status, KVEntry value) {
        super(id, status);
        this.value = value;
    }

    public KVEntry getValue() {
        return value;
    }
}
