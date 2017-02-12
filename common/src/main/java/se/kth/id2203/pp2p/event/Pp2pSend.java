package se.kth.id2203.pp2p.event;

import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.network.Address;

/**
 * Created by ralambom on 11/02/17.
 */
public final class Pp2pSend implements KompicsEvent{

    private Pp2pDeliver deliverMsg;
    private Address destination;

    public Pp2pSend(Address destination, Pp2pDeliver deliverMsg) {
        this.deliverMsg = deliverMsg;
        this.destination = destination;
    }

    public Pp2pDeliver getDeliverMsg() {
        return deliverMsg;
    }

    public Address getDestination() {
        return destination;
    }
}
