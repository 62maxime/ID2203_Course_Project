package se.kth.id2203.pp2p.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 11/02/17.
 */
public final class Pp2pDeliver implements KompicsEvent{

    private NetAddress source;
    private KompicsEvent payload;

    public Pp2pDeliver(NetAddress source, KompicsEvent payload) {
        this.source = source;
        this.payload = payload;
    }

    public NetAddress getSource() {
        return source;
    }

    public KompicsEvent getPayload() {
        return payload;
    }
}
