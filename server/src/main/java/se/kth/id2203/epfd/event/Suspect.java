package se.kth.id2203.epfd.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

/**
 * Created by ralambom on 12/02/17.
 */
public class Suspect implements KompicsEvent {

    private final NetAddress source;

    public Suspect(NetAddress source) {
        this.source = source;
    }

    public NetAddress getSource() {
        return source;
    }
}
