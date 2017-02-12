package se.kth.id2203.epfd.port;

import se.kth.id2203.epfd.event.Restore;
import se.kth.id2203.epfd.event.Suspect;
import se.sics.kompics.PortType;

/**
 * Created by ralambom on 12/02/17.
 */
public class EventuallyPerfectFailureDetector extends PortType {

    {
        indication(Suspect.class);
        indication(Restore.class);
    }

}
