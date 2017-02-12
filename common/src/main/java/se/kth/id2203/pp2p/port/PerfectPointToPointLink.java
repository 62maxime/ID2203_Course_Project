package se.kth.id2203.pp2p.port;

import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.kth.id2203.pp2p.event.Pp2pSend;
import se.sics.kompics.PortType;

/**
 * Created by ralambom on 12/02/17.
 */
public class PerfectPointToPointLink extends PortType {
    {
        indication(Pp2pDeliver.class);
        indication(Pp2pSend.class);
    }
}
