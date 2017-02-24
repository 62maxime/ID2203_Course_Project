package se.kth.id2203.multipaxos.component;

import se.kth.id2203.kvstore.Operation;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ralambom on 23/02/17.
 */
public class ReadItem implements Serializable {

    private static final long serialVersionUID = 7547738871874789409L;

    private int ts;
    private ArrayList<Operation> vsuf;

    public ReadItem(int ts, ArrayList<Operation> vsuf) {
        this.ts = ts;
        this.vsuf = vsuf;
    }

    public int getTs() {
        return ts;
    }

    public ArrayList<Operation> getVsuf() {
        return vsuf;
    }
}
