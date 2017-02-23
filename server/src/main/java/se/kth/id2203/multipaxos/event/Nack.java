package se.kth.id2203.multipaxos.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class Nack extends NetMessage implements Serializable {

    private static final long serialVersionUID = 1075509236957913278L;

    private int pts;
    private int t;

    public Nack(NetAddress src, NetAddress dst, int pts, int t) {
        super(src, dst, Transport.TCP);
        this.pts = pts;
        this.t = t;
    }

    public int getPts() {
        return pts;
    }

    public int getT() {
        return t;
    }

    @Override
    public String toString() {
        return "Nack{" +
                "pts=" + pts +
                ", t=" + t +
                '}';
    }
}
