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
public class PrepareAck extends NetMessage implements Serializable {

    private static final long serialVersionUID = -5670856625275896226L;

    private int pts;
    private int ts;
    private ArrayList<Operation> vsuf;
    private int l;
    private int t;

    public PrepareAck(NetAddress src, NetAddress dst, int pts, int ts, ArrayList<Operation> vsuf, int l, int t) {
        super(src, dst, Transport.TCP);
        this.pts = pts;
        this.ts = ts;
        this.vsuf = vsuf;
        this.l = l;
        this.t = t;
    }

    public int getPts() {
        return pts;
    }

    public int getTs() {
        return ts;
    }

    public ArrayList<Operation> getVsuf() {
        return vsuf;
    }

    public int getL() {
        return l;
    }

    public int getT() {
        return t;
    }
}
