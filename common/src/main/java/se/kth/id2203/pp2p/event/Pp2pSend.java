package se.kth.id2203.pp2p.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;

/**
 * Created by ralambom on 11/02/17.
 */
public final class Pp2pSend implements KompicsEvent{

    private NetAddress destination;
    private KompicsEvent payload;

    public Pp2pSend(NetAddress destination, KompicsEvent payload) {
        this.payload = payload;
        this.destination = destination;
    }

    public NetAddress getDestination() {
        return destination;
    }

    public KompicsEvent getPayload() {
        return payload;
    }
}