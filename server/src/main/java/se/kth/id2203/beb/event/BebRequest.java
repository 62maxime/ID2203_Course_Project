package se.kth.id2203.beb.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;
import java.util.Collection;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebRequest implements KompicsEvent, Serializable {
    private static final long serialVersionUID = -7348390257691821188L;
    public final KompicsEvent payload;
    private final Collection<NetAddress> specifiedTopology;

    public BebRequest(KompicsEvent payload, Collection<NetAddress> specifiedTopology) {
        this.payload = payload;
        this.specifiedTopology = specifiedTopology;
    }

    public BebRequest(final KompicsEvent payload) {
        this.payload = payload;
        this.specifiedTopology = null;

    }

    public Collection<NetAddress> getSpecifiedTopology() {
        return specifiedTopology;
    }
}
