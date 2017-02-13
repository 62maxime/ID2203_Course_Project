package se.kth.id2203.pp2p.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;

/**
 * Created by ralambom on 11/02/17.
 */
public final class Pp2pSend implements KompicsEvent{

    private Pp2pDeliver deliverMsg;
    private NetAddress destination;

    public Pp2pSend(NetAddress destination, Pp2pDeliver deliverMsg) {
        this.deliverMsg = deliverMsg;
        this.destination = destination;
    }

    public Pp2pDeliver getDeliverMsg() {
        return deliverMsg;
    }

    public NetAddress getDestination() {
        return destination;
    }
}
