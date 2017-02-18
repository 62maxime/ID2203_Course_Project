package se.kth.id2203.simulation.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.epfd.component.Epfd;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.event.ListenTo;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

/**
 * Created by YannL on 16/02/2017.
 */
public class Monitored extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(Monitored.class);

    //******* Ports ******
    Positive<Network> network = requires(Network.class);
    Positive<Timer> timer = requires(Timer.class);
    //******* Fields ******
    private final NetAddress self;

    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            LOG.info("[Monitored] Node " + self + " started ");
        }
    };

    protected final Handler<Kill> killHandler = new Handler<Kill>() {
        @Override
        public void handle(Kill kill) {
            LOG.info("[Monitored] Node " + self + " killed ");
        }
    };

    public Monitored(Init init) {
        this.self = init.self;

        Component epfd = create(Epfd.class, new EpfdInit(self, 1000, 4000));

        connect(epfd.getNegative(Network.class), network, Channel.TWO_WAY);
        connect(epfd.getNegative(Timer.class), timer, Channel.TWO_WAY);

        subscribe(startHandler, control);
        subscribe(killHandler, control);
    }


    public static class Init extends se.sics.kompics.Init<Monitored> {
        public final NetAddress self;

        public Init(NetAddress self) {
            this.self = self;
        }
    }
}