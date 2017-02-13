package se.kth.id2203.epfd.component;


import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.networking.NetMessage;
import se.sics.kompics.network.Transport;

import java.io.Serializable;

/**
 * Created by ralambom on 11/02/17.
 */
public class HeartbeatRequest extends NetMessage implements Serializable{

    private static final long serialVersionUID = 2157271902369443307L;
    private int seqnum;

    public HeartbeatRequest(NetAddress source, NetAddress dest, int seqnum) {
        super(source, dest, Transport.TCP);
        this.seqnum = seqnum;
    }

    public int getSeqnum() {
        return seqnum;
    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }
}
