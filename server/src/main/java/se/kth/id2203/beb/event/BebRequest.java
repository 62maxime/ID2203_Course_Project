package se.kth.id2203.beb.event;

import se.sics.kompics.KompicsEvent;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebRequest implements KompicsEvent {
    public final KompicsEvent payload;

    public BebRequest(final KompicsEvent payload) {
        this.payload = payload;
    }
}
