package se.kth.id2203.fpl.port;

import se.kth.id2203.fpl.event.FplDeliver;
import se.kth.id2203.fpl.event.FplSend;
import se.kth.id2203.pp2p.component.PerfectPointToPointLink;
import se.sics.kompics.PortType;

/**
 * Created by YannL on 22/02/2017.
 */
public class FplPort extends PortType {
    {
        indication(FplDeliver.class);
        request(FplSend.class);
    }
}
