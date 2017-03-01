package se.kth.id2203.multipaxos.event;

import se.kth.id2203.overlay.ReplicationGroup;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class MPgroup implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -4534971793387045645L;
    private final ReplicationGroup group;

    public MPgroup(ReplicationGroup group) {
        this.group = group;
    }

    public ReplicationGroup getGroup() {
        return group;
    }
}
