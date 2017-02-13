package se.kth.id2203.epfd.component;


import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pDeliver;

import java.io.Serializable;

/**
 * Created by ralambom on 11/02/17.
 */
public class HeartbeatReply extends Pp2pDeliver implements Serializable{

    private static final long serialVersionUID = -4714284515301056909L;
    private int seqnum;

    public HeartbeatReply(NetAddress source, int seqnum) {
        super(source);
        this.seqnum = seqnum;
    }

    public int getSeqnum() {
        return seqnum;
    }

    public void setSeqnum(int seqnum) {
        this.seqnum = seqnum;
    }
}
