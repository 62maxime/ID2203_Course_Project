package se.kth.id2203.beb.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.event.BebTopology;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.common.port.GroupTopology;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
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
    private Set<NetAddress> topology;
    //******* Handlers ******
    protected final Handler<BebRequest> requestHandler = new Handler<BebRequest>() {
        @Override
        public void handle(BebRequest bebRequest) {
            LOG.info("[BebBroadcast] BebRequest received by " + self.toString());
            LOG.debug("[BebBroadcast] Topology = " + topology.toString());
            for (NetAddress adr : topology) {
                trigger(new Message(self, adr, bebRequest), net);
                LOG.debug("[BebBroadcast] Payload sent to " + adr.toString());
            }
        }
    };

    protected final ClassMatchedHandler<BebRequest, Message> deliverHandler = new ClassMatchedHandler<BebRequest, Message>() {
        @Override
        public void handle(BebRequest bebRequest, Message message) {
            LOG.debug("[BebBroadcast] Network message from " + message.getSource() + " received by " + self.toString());
            BebDeliver bebDeliver = new BebDeliver(message.getSource(), bebRequest.payload);
            trigger(bebDeliver, beb);
            LOG.info("[BebBroadcast] BebDeliver delivered by " + self.toString());
        }
    };
    protected final ClassMatchedHandler<GroupTopology,Message> topologyMessageHandler = new ClassMatchedHandler<GroupTopology, Message>() {
        @Override
        public void handle(GroupTopology topo, Message message) {
            LOG.debug("Received Topology " + topo.getTopology().toString());
            topology.clear();
            topology.addAll(topo.getTopology());
        }
    };

    protected final Handler<BebTopology> topologyHandler = new Handler<BebTopology>() {
        @Override
        public void handle(BebTopology bebTopology) {
            LOG.debug("[BebBroadcast] BebTopology event received by " + self.toString());
            topology = bebTopology.getTopology();
            LOG.debug("[BebBroadcast]New topology = " + topology.toString());
        }
    };

    public BestEffortBroadcast(BebInit init) {
        this.self = init.self;
        this.topology = init.topology;

        subscribe(requestHandler, beb);
        subscribe(deliverHandler, net);
        subscribe(topologyMessageHandler, net);
    }
}
