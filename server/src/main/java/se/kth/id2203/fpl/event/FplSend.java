package se.kth.id2203.fpl.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by YannL on 22/02/2017.
 */
public class FplSend implements KompicsEvent, Serializable {

    private final NetAddress recipient;
    private final KompicsEvent message;

    public FplSend(NetAddress recipient, KompicsEvent message) {
        this.recipient = recipient;
        this.message = message;
    }

    public NetAddress getRecipient() {
        return recipient;
    }

    public KompicsEvent getMessage() {
        return message;
    }
}
