package se.kth.id2203.kvstore;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

/**
 * Created by 62maxime on 13/02/2017.
 */
public class GetRequest extends Operation implements KompicsEvent {

    public GetRequest(String key) {
        super(key);
    }

    public GetRequest(String key, NetAddress source) {
        super(key, source);
    }
}
