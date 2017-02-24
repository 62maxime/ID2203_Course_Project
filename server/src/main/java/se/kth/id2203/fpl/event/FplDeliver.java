package se.kth.id2203.fpl.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by YannL on 22/02/2017.
 */
public class FplDeliver implements KompicsEvent, Serializable {

    private final NetAddress sender;
    private final KompicsEvent message;

    public FplDeliver(NetAddress sender, KompicsEvent message) {
        this.sender = sender;
        this.message = message;
    }

    public NetAddress getSender() {
        return sender;
    }

    public KompicsEvent getMessage() {
        return message;
    }
}
