package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Read_Response implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 7272451227450461928L;
    private KVEntry value;

    public AR_Read_Response(KVEntry value) {
        this.value = value;
    }

    public KVEntry getValue() {
        return value;
    }
}
