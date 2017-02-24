package se.kth.id2203.multipaxos.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class Decide extends NetMessage implements Serializable {

    private static final long serialVersionUID = -5186735275290135589L;

    private int ts;
    private int l;
    private int t;

    public Decide(NetAddress src, NetAddress dst, int ts, int l, int t) {
        super(src, dst, Transport.TCP);
        this.ts = ts;
        this.l = l;
        this.t = t;
    }

    public int getTs() {
        return ts;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }

    @Override
    public String toString() {
        return "Decide{" +
                "ts=" + ts +
                ", l=" + l +
                ", t=" + t +
                '}';
    }
}
