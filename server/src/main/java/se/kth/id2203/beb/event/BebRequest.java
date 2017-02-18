package se.kth.id2203.beb.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebRequest implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -7348390257691821188L;
    public final KompicsEvent payload;

    public BebRequest(final KompicsEvent payload) {
        this.payload = payload;
    }
}
