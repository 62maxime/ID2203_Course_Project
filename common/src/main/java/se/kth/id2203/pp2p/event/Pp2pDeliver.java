package se.kth.id2203.pp2p.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 11/02/17.
 */
public abstract class Pp2pDeliver implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -314262496401376431L;
    private NetAddress source;

    public Pp2pDeliver(NetAddress source) {
        this.source = source;
    }

    public NetAddress getSource() {
        return source;
    }
}
