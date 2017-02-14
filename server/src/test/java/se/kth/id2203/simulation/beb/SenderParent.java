package se.kth.id2203.simulation.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.component.BebInit;
import se.kth.id2203.beb.component.BestEffortBroadcast;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class SenderParent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(SenderParent.class);

    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public SenderParent(Init init) {
        Component sender = create(Sender.class, new Sender.Init(init.self));
        Component beb = create(BestEffortBroadcast.class, new BebInit(init.self, init.recipients));

        connect(sender.getNegative(BebPort.class), beb.getPositive(BebPort.class), Channel.TWO_WAY);
        connect(beb.getNegative(Network.class), network, Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<SenderParent> {

        public final NetAddress self;
        public final Set<NetAddress> recipients;

        public Init(NetAddress self, Set<NetAddress> recipients) {
            //LOG.info("[SenderParent] Topology = " + recipients);
            this.self = self;
            this.recipients = recipients;
        }
    }
}
