package se.kth.id2203.multipaxos.port;

import se.kth.id2203.multipaxos.event.AscAbort;
import se.kth.id2203.multipaxos.event.AscDecide;
import se.kth.id2203.multipaxos.event.AscPropose;
import se.kth.id2203.multipaxos.event.MPgroup;
import se.sics.kompics.PortType;

/**
 * Created by ralambom on 23/02/17.
 */
public class AscPort extends PortType {

    {
        request(AscPropose.class);
        indication(AscDecide.class);
        indication(AscAbort.class);
        request(MPgroup.class);
    }

}
