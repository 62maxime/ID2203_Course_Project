package se.kth.id2203.simulation.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.component.BebInit;
import se.kth.id2203.beb.component.BestEffortBroadcast;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.epfd.component.Epfd;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.simulation.beb.Sender;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.Set;

/**
 * Created by YannL on 16/02/2017.
 */
public class DetectorParent extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(DetectorParent.class);

    Positive<Network> net = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);

    public DetectorParent(Init init) {
        Component detector = create(Detector.class, new Detector.Init(init.self, init.topology));
        Component epfd = create(Epfd.class, new EpfdInit(init.self, 1000, 4000));

        connect(epfd.getPositive(EventuallyPerfectFailureDetector.class), detector.getNegative(EventuallyPerfectFailureDetector.class),  Channel.TWO_WAY);
        connect(timer, detector.getNegative(Timer.class),  Channel.TWO_WAY);
        connect(net, epfd.getNegative(Network.class), Channel.TWO_WAY);
        connect(timer, epfd.getNegative(Timer.class), Channel.TWO_WAY);
    }

    public static class Init extends se.sics.kompics.Init<DetectorParent> {
        public final NetAddress self;
        public final Set<NetAddress> topology;

        public Init(NetAddress self, Set<NetAddress> topology) {
            this.self = self;
            this.topology = topology;
        }
    }
}
