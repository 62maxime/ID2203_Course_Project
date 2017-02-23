package se.kth.id2203.multipaxos.event;

import se.kth.id2203.kvstore.Operation;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ralambom on 23/02/17.
 */
public class Accept extends NetMessage implements Serializable {

    private static final long serialVersionUID = -8643998975549121318L;

    private int ts;
    private ArrayList<Operation> vsuf;
    private int offs;
    private int t;

    public Accept(NetAddress src, NetAddress dst, int ts, ArrayList<Operation> vsuf, int offs, int t) {
        super(src, dst, Transport.TCP);
        this.ts = ts;
        this.vsuf = vsuf;
        this.offs = offs;
        this.t = t;
    }

    public int getTs() {
        return ts;
    }

    public ArrayList<Operation> getVsuf() {
        return vsuf;
    }

    public int getOffs() {
        return offs;
    }

    public int getT() {
        return t;
    }
}
