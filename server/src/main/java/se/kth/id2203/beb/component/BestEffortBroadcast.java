package se.kth.id2203.beb.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.kth.id2203.pp2p.event.Pp2pSend;
import se.kth.id2203.pp2p.port.Pp2pPort;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;

import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class BestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(BestEffortBroadcast.class);
    //******* Ports ******
    protected final Negative<BebPort> beb = provides(BebPort.class);
    protected final Positive<Pp2pPort> epfd = requires(Pp2pPort.class);
    //******* Fields ******
    private final NetAddress self;
    private final Set<NetAddress> topology;
    //******* Handlers ******
    protected final Handler<BebRequest> requestHandler = new Handler<BebRequest>() {

        @Override
        public void handle(BebRequest bebRequest) {
            LOG.info("[BebBroadcast] BebRequest received by " + self.toString());
            for (NetAddress adr : topology) {
                Pp2pSend pp2Send = new Pp2pSend(adr, bebRequest.payload);
                trigger(pp2Send, epfd);
                LOG.info("[BebBroadcast] Payload sent to " + adr.toString());
            }
        }
    };


    protected final Handler<Pp2pDeliver> deliverHandler = new Handler<Pp2pDeliver>() {
        @Override
        public void handle(Pp2pDeliver pp2pDeliver) {
            LOG.info("[BebBroadcast] Pp2pDeliver received");
            BebDeliver bebDeliver = new BebDeliver(pp2pDeliver.getSource(), null); //TODO
            trigger(bebDeliver, beb);
            LOG.info("[BebBroadcast] BebDeliver delivered by " + self.toString());
        }
    };

    public BestEffortBroadcast(BebInit init) {
        this.self = init.self;
        this.topology = init.topology;
    }
}
