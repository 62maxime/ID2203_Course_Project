package se.kth.id2203.multipaxos.event;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class AcceptAck extends NetMessage implements Serializable {

    private static final long serialVersionUID = -815777783197609168L;

    private int pts;
    private int l;
    private int t;

    public AcceptAck(NetAddress src, NetAddress dst, int pts, int l, int t) {
        super(src, dst, Transport.TCP);
        this.pts = pts;
        this.l = l;
        this.t = t;
    }

    public int getPts() {
        return pts;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }
}
