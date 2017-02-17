package se.kth.id2203.sharedmemory.event;

import com.google.common.base.Optional;
import se.kth.id2203.kvstore.KVEntry;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class AR_Read_Response implements KompicsEvent, Serializable {

    private Optional<KVEntry> value;

    public AR_Read_Response(Optional<KVEntry> value) {
        this.value = value;
    }

    public Optional<KVEntry> getValue() {
        return value;
    }
}
