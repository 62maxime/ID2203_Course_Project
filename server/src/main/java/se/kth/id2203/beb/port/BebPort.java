package se.kth.id2203.beb.port;

import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.sics.kompics.PortType;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebPort extends PortType {
    {
        indication(BebDeliver.class);
        request(BebRequest.class);
    }
}
