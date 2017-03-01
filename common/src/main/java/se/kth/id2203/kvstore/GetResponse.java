package se.kth.id2203.kvstore;

import com.google.common.base.MoreObjects;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 13/02/2017.
 */
public class GetResponse extends OpResponse implements KompicsEvent, Serializable{

    private static final long serialVersionUID = 8124107397103100467L;
    private KVEntry value;

    public GetResponse(UUID id, Code status, KVEntry value) {
        super(id, status);
        this.value = value;
    }

    public KVEntry getValue() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("status", status)
                .add("value", value)
                .toString();
    }

}
