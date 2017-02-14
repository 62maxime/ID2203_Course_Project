package se.kth.id2203.beb.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Negative;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class BestEffortBroadcast extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(BestEffortBroadcast.class);
    //******* Ports ******
    protected final Negative<BebPort> beb = provides(BebPort.class);
    protected final Positive<Network> net = requires(Network.class);
    //******* Fields ******
    private final NetAddress self;
    private final Set<NetAddress> topology;
    //******* Handlers ******
    protected final Handler<BebRequest> requestHandler = new Handler<BebRequest>() {
        @Override
        public void handle(BebRequest bebRequest) {
            LOG.info("[BebBroadcast] BebRequest received by " + self.toString());
            LOG.info("[BebBroadcast] Topology = " + topology.toString());
            for (NetAddress adr : topology) {
                trigger(new Message(self, adr, bebRequest.payload), net);
                LOG.info("[BebBroadcast] Payload sent to " + adr.toString());
            }
        }
    };


    protected final Handler<Message> deliverHandler = new Handler<Message>() {
        @Override
        public void handle(Message payload) {
            LOG.info("[BebBroadcast] Network message received by " + self.toString());
            BebDeliver bebDeliver = new BebDeliver(payload.getSource(), payload.payload);
            trigger(bebDeliver, beb);
            LOG.info("[BebBroadcast] BebDeliver delivered by " + self.toString());
        }
    };

    public BestEffortBroadcast(BebInit init) {
        this.self = init.self;
        this.topology = init.topology;

        subscribe(requestHandler, beb);
        subscribe(deliverHandler, net);
    }
}
