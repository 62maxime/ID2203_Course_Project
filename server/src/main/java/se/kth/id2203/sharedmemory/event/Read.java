package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Read implements KompicsEvent, Serializable {

    private int rid;

    public Read(int rid) {
        this.rid = rid;
    }

    public int getRid() {
        return rid;
    }
}
