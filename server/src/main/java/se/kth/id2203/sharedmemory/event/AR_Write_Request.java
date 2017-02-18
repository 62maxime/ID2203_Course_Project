package se.kth.id2203.sharedmemory.event;

import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Write_Request implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 3210761070543736747L;
    private KVEntry value;

    public AR_Write_Request(KVEntry value) {
        this.value = value;
    }

    public KVEntry getValue() {
        return value;
    }
}
