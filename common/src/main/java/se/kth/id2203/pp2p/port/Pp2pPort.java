package se.kth.id2203.pp2p.port;

import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.kth.id2203.pp2p.event.Pp2pSend;
import se.sics.kompics.PortType;

/**
 * Created by ralambom on 12/02/17.
 */
public class Pp2pPort extends PortType {
    {
        request(Pp2pSend.class);
        indication(Pp2pDeliver.class);
    }
}
