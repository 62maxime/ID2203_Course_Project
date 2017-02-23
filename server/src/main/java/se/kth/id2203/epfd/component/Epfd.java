package se.kth.id2203.epfd.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.epfd.event.ListenTo;
import se.kth.id2203.epfd.event.Reset;
import se.kth.id2203.epfd.event.Restore;
import se.kth.id2203.epfd.event.Suspect;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelTimeout;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.UUID;

/**
 * Created by ralambom on 11/02/17.
 */
public class Epfd extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(Epfd.class);

    //EPFD subscriptions
    private Positive<Timer> timer = requires(Timer.class);
    private Positive<Network> pLink = requires(Network.class);
    private Negative<EventuallyPerfectFailureDetector> epfd = provides(EventuallyPerfectFailureDetector.class);

    // EPDF component state and initialization

    //configuration parameters
    private NetAddress self;
    private HashSet<NetAddress> topology = new HashSet<>();
    private long delta;

    //mutable state
    private long period;
    private HashSet<NetAddress> alive = new HashSet<>();
    private HashSet<NetAddress> suspected = new HashSet<>();
    private int seqnum = 0;

    //Timeout Id
    private UUID timerId;

    public Epfd(EpfdInit init) {
        this.self = init.getSelfAddress();
        //this.topology.add(init.getSelfAddress());
        this.delta = init.getDelta();

        this.period = init.getInitialPeriod();
        this.alive.add(init.getSelfAddress());

    }

    private void startTimer(long period) {
        ScheduleTimeout scheduledTimeout = new ScheduleTimeout(period);
        scheduledTimeout.setTimeoutEvent(new CheckTimeout(scheduledTimeout));
        trigger(scheduledTimeout, timer);
        timerId = scheduledTimeout.getTimeoutEvent().getTimeoutId();
    }

    //Handlers

    private Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            startTimer(period);
        }
    };


    private Handler<CheckTimeout> checkTimeoutHandler = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout checkTimeout) {
            for (NetAddress address : alive) {
                if (suspected.contains(address)) {
                    period += delta;
                    break;
                }
            }
            seqnum++;
            for (NetAddress p : topology) {
                if (!alive.contains(p) && !suspected.contains(p)) {
                    suspected.add(p);
                    trigger(new Suspect(p), epfd);
                } else if (alive.contains(p) && suspected.contains(p)) {
                    suspected.remove(p);
                    trigger(new Restore(p), epfd);
                }
                trigger(new HeartbeatRequest(self, p, seqnum), pLink);
            }
            alive.clear();
            startTimer(period);
        }
    };

    private Handler<HeartbeatRequest> heartbeatReqHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest heartbeatRequest) {
            LOG.debug(self.toString() + " receives a heartbeat request from " + heartbeatRequest.getSource());
            trigger(new HeartbeatReply(self, heartbeatRequest.getSource(), heartbeatRequest.getSeqnum()), pLink);
        }
    };

    private Handler<HeartbeatReply> heartbeatRepHandler = new Handler<HeartbeatReply>() {
        @Override
        public void handle(HeartbeatReply heartbeatReply) {
            LOG.debug(self.toString() + " receives a heartbeat reply from " + heartbeatReply.getSource()
                    + "with seqnum " + heartbeatReply.getSeqnum() + " internal " + seqnum);

            if (topology.contains(heartbeatReply.getSource())) {
                if (heartbeatReply.getSeqnum() == seqnum || suspected.contains(heartbeatReply.getSource())) {
                    alive.add(heartbeatReply.getSource());
                }
            }
        }
    };

    private Handler<ListenTo> listenToHandler = new Handler<ListenTo>() {
        @Override
        public void handle(ListenTo listenTo) {
            LOG.debug("ListenTo from " + self + " for " + listenTo.getAddresses().toString());
            trigger(new CancelTimeout(timerId), timer);
            topology.addAll(listenTo.getAddresses());
            alive.addAll(listenTo.getAddresses());
            startTimer(period);
        }
    };

    private Handler<Reset> resetHandler = new Handler<Reset>() {
        @Override
        public void handle(Reset reset) {
            LOG.debug("Reset from" + self);
            topology.clear();
            trigger(new CancelTimeout(timerId), timer);
            alive.clear();

            self = reset.getInit().getSelfAddress();
            //topology.add(reset.getInit().getSelfAddress());
            delta = reset.getInit().getDelta();
            period = reset.getInit().getInitialPeriod();
            //alive.add(reset.getInit().getSelfAddress());

            startTimer(period);
        }
    };

    {

        subscribe(startHandler, control);
        subscribe(checkTimeoutHandler, timer);
        subscribe(heartbeatReqHandler, pLink);
        subscribe(heartbeatRepHandler, pLink);
        subscribe(listenToHandler, epfd);
        subscribe(resetHandler, epfd);
    }

}
