package se.kth.id2203.leaderdetection.event;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 22/02/2017.
 */
public class Trust implements KompicsEvent, Serializable {


    private static final long serialVersionUID = -8577715785810669990L;
    private NetAddress leader;

    public Trust(NetAddress leader) {
        this.leader = leader;
    }

    public NetAddress getLeader() {
        return leader;
    }
}
