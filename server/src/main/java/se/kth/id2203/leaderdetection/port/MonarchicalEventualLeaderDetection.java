package se.kth.id2203.leaderdetection.port;

import se.kth.id2203.leaderdetection.event.Trust;
import se.sics.kompics.PortType;

/**
 * Created by 62maxime on 22/02/2017.
 */
public class MonarchicalEventualLeaderDetection extends PortType {
    {
        indication(Trust.class);
    }
}
