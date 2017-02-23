package se.kth.id2203.multipaxos.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class Prepare extends NetMessage implements Serializable {

    private static final long serialVersionUID = -4542975721027775359L;

    private int ts;
    private int l;
    private int t;

    public Prepare(NetAddress src, NetAddress dst, int pts, int al, int t) {
        super(src, dst, Transport.TCP);
        this.ts = pts;
        this.l = al;
        this.t = t;
    }

    public int getT() {
        return t;
    }

    public int getTs() {
        return ts;
    }

    public int getL() {
        return l;
    }
}
