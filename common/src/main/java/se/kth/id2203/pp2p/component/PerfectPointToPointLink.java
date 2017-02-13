package se.kth.id2203.pp2p.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pDeliver;
import se.kth.id2203.pp2p.event.Pp2pSend;
import se.kth.id2203.pp2p.port.Pp2pPort;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class PerfectPointToPointLink extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(PerfectPointToPointLink.class);
    //******* Ports ******
    protected final Negative<Pp2pPort> pp2p = provides(Pp2pPort.class);
    protected final Positive<Network> net = requires(Network.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private final Set<KompicsEvent> delivered = new HashSet<>();
    //******* Handlers ******
    protected final Handler<Pp2pSend> pp2pSendHandler = new Handler<Pp2pSend>() {
        @Override
        public void handle(Pp2pSend pp2pSend) {
            LOG.info("[Pp2pLink] Pp2pSend received by " + self.toString());
            trigger(new Message(self, pp2pSend.getDestination(), pp2pSend.getPayload()), net);
            LOG.info("[Pp2pLink] NetMessage sent to " + pp2pSend.getDestination().toString());
        }
    };

    protected final ClassMatchedHandler<KompicsEvent, Message> pp2pDeliverHandler =
            new ClassMatchedHandler<KompicsEvent, Message>() {
        @Override
        public void handle(KompicsEvent pp2pDeliver, Message m) {
            LOG.info("[Pp2pLink] NetMessage received from " + m.getSource().toString());
            KompicsEvent payload = m.payload;
            if (!delivered.contains(payload)) {
                delivered.add(payload);
                trigger(new Pp2pDeliver(m.getSource(), payload), pp2p);
                LOG.info("[Pp2pLink] Pp2pDeliver delivered to " + self.toString());
            }
        }
    };

    {
        subscribe(pp2pSendHandler, pp2p);
        subscribe(pp2pDeliverHandler, net);
    }
}
