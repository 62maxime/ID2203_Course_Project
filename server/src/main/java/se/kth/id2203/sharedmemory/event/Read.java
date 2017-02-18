package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Read implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 7229746719790140726L;
    private int rid;
    private int key;

    public Read(int rid, int key) {
        this.rid = rid;
        this.key = key;
    }

    public int getRid() {
        return rid;
    }

    public int getKey() {
        return key;
    }
}
