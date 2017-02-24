package se.kth.id2203.kvstore;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 18/02/2017.
 */
public class PutRequest extends Operation implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -8863874946196576992L;
    private KVEntry value;

    public PutRequest(String key, NetAddress source, KVEntry value) {
        super(key, source);
        this.value = value;
    }

    public PutRequest(String key, KVEntry value) {
        super(key);
        this.value = value;
    }

    public KVEntry getValue() {
        return value;
    }
}
