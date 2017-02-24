package se.kth.id2203.multipaxos.event;

import se.kth.id2203.overlay.ReplicationGroup;
import se.sics.kompics.KompicsEvent;

/**
 * Created by ralambom on 23/02/17.
 */
public class MPgroup implements KompicsEvent {

    private final ReplicationGroup group;

    public MPgroup(ReplicationGroup group) {
        this.group = group;
    }

    public ReplicationGroup getGroup() {
        return group;
    }
}
