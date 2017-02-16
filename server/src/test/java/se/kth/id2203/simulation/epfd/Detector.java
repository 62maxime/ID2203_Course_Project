package se.kth.id2203.simulation.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.epfd.event.ListenTo;
import se.kth.id2203.epfd.event.Restore;
import se.kth.id2203.epfd.event.Suspect;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;
import se.sics.kompics.timer.Timer;

import java.util.Set;


/**
 * Created by YannL on 13/02/2017.
 */
public class Detector extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(Detector.class);
    //******* Ports ******
    protected final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //******* Fields ******
    private final NetAddress self;
    private Set<NetAddress> topology;

    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            LOG.info("[Detector] Start Detector " + self + " on topology : " + topology.toString());
            trigger(new ListenTo(topology), epfd);
        }
    };


    protected final Handler<Restore> restoreHandler = new Handler<Restore>() {
        @Override
        public void handle(Restore restore) {
            LOG.info("[Detector] Node " + restore.getSource() + " restored !");
        }
    };

    protected final Handler<Suspect> suspectHandler = new Handler<Suspect>() {
        @Override
        public void handle(Suspect suspect) {
            LOG.info("[Detector] Node " + suspect.getSource() + " suspected !");
        }
    };


    public Detector(Init init) {
        this.self = init.self;
        this.topology = init.topology;

        subscribe(startHandler, control);
        subscribe(restoreHandler, epfd);
        subscribe(suspectHandler, epfd);
    }

    public static class Init extends se.sics.kompics.Init<Detector> {
        public final NetAddress self;
        public final Set<NetAddress> topology;

        public Init(NetAddress self, Set<NetAddress> topology) {
            this.self = self;
            this.topology = topology;
        }
    }
}
