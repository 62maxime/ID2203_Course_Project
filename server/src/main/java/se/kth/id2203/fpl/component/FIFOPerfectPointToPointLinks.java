package se.kth.id2203.fpl.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.fpl.event.FplDeliver;
import se.kth.id2203.fpl.event.FplMessage;
import se.kth.id2203.fpl.event.FplSend;
import se.kth.id2203.fpl.port.FplPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by YannL on 22/02/2017.
 */
public class FIFOPerfectPointToPointLinks extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(FIFOPerfectPointToPointLinks.class);
    //******* Ports ******
    protected final Negative<FplPort> fpl = provides(FplPort.class);
    protected final Positive<Network> net = requires(Network.class);
    //******* Fields ******
    private final NetAddress self;
    private Set<NetAddress> topology;
    private Map<NetAddress, Integer> lsn;
    private Map<NetAddress, Integer> next;
    private List<PendingMessage> pending = new LinkedList<>(); // Could be optimized with a SortedList
    private boolean busy = false;
    //******* Handlers ******
    protected final Handler<FplSend> sendHandler = new Handler<FplSend>() {
        @Override
        public void handle(FplSend fplSend) {
            LOG.info("[FIFO_PL] FplSend received by " + self.toString());
            NetAddress recipient = fplSend.getRecipient();
            if (topology.contains(recipient)) {
                int messOrder = lsn.containsKey(recipient) ? lsn.get(recipient) : 0;
                lsn.put(recipient, messOrder + 1);
                trigger(new Message(self, recipient, new FplMessage(fplSend.getMessage(), lsn.get(recipient))), net);
                LOG.info("[FIFO_PL] FplMessage sent to " + recipient);
            }
        }
    };

    protected final ClassMatchedHandler<FplMessage, Message> deliverHandler = new ClassMatchedHandler<FplMessage, Message>() {
        @Override
        public void handle(FplMessage fplMessage, Message message) {
            LOG.info("[FIFO_PL] FplMessage received by " + self.toString() + " from " + message.getSource());
            pending.add(new PendingMessage(message.getSource(), fplMessage));
            //if (!busy) {
            //    busy = true;
            //boolean exist;
            //do {
            //    exist = false;
            for (PendingMessage pendMess : pending) {
                NetAddress sender = pendMess.getSender();
                int nextMessage = next.containsKey(sender) ? next.get(sender) : 1;
                if (pendMess.getSenderOrder().equals(nextMessage)) {
                    next.put(sender, nextMessage + 1);
                    pending.remove(pendMess);
                    trigger(new FplDeliver(sender, pendMess.getMessage()), fpl);
                    LOG.info("[FIFO_PL] Message from " + sender + " delivered by " + self);
                            //           exist = true;
                }
            }
            //} while (exist);
            //busy = false;
        }
    };

    public FIFOPerfectPointToPointLinks(NetAddress self,
                                        Set<NetAddress> topology,
                                        Map<NetAddress, Integer> lsn,
                                        Map<NetAddress, Integer> next) {
        this.self = self;
        this.topology = topology;
        this.lsn = lsn;
        this.next = next;

        subscribe(sendHandler, fpl);
        subscribe(deliverHandler, net);
    }


    private class PendingMessage {
        private NetAddress sender;
        private FplMessage message;

        public PendingMessage(NetAddress sender, FplMessage message) {
            this.sender = sender;
            this.message = message;
        }

        public NetAddress getSender() {
            return sender;
        }

        public KompicsEvent getMessage() {
            return message.getMessage();
        }

        public Integer getSenderOrder() {
            return message.getOrderSender();
        }
    }
}
