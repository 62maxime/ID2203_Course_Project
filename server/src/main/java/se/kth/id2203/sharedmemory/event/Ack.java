package se.kth.id2203.sharedmemory.event;

import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class Ack implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -1025410931340346861L;
    private int rid;
    private int key;


    public Ack(int rid, int key) {

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
