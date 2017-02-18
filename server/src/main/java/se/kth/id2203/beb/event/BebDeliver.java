package se.kth.id2203.beb.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.sharedmemory.event.Read;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

import java.io.Serializable;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebDeliver implements KompicsEvent, PatternExtractor<Class, KompicsEvent>, Serializable {

    private static final long serialVersionUID = 3741009865342768900L;
    public final NetAddress source;
    public final KompicsEvent payload;

    public BebDeliver(final NetAddress source, final KompicsEvent payload) {
        this.source = source;
        this.payload = payload;
    }

    @Override
    public Class extractPattern() {
        return payload.getClass();
    }

    @Override
    public KompicsEvent extractValue() {
        return payload;
    }
}
