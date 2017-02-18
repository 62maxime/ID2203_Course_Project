package se.kth.id2203.kvstore;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by 62maxime on 18/02/2017.
 */
public class PutResponse extends OpResponse implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -7476821099171749051L;

    public PutResponse(UUID id, Code status) {
        super(id, status);
    }


}
