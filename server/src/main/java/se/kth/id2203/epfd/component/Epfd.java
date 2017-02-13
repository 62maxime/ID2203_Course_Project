package se.kth.id2203.epfd.component;

import se.kth.id2203.epfd.event.Restore;
import se.kth.id2203.epfd.event.Suspect;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.pp2p.event.Pp2pSend;
import se.kth.id2203.pp2p.port.PerfectPointToPointLink;
import se.sics.kompics.*;
import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;

/**
 * Created by ralambom on 11/02/17.
 */
public class Epfd extends ComponentDefinition {

    //EPFD subscriptions
    private Positive<Timer> timer = requires(Timer.class);
    private Positive<PerfectPointToPointLink> pLink = requires(PerfectPointToPointLink.class);
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

    public Epfd(EpfdInit init) {
        this.self = init.getSelfAddress();
        this.topology.addAll(init.getAllAddresses());
        this.delta = init.getDelta();

        this.period = init.getInitialPeriod();
        this.alive.addAll(init.getAllAddresses());

        subscribe(startHandler, control);
        subscribe(checkTimeoutHandler, timer);
        subscribe(heartbeatReqHandler, pLink);
        subscribe(heartbeatRepHandler, pLink);
    }

    private void startTimer(long period) {
        ScheduleTimeout scheduledTimeout = new ScheduleTimeout(period);
        scheduledTimeout.setTimeoutEvent(new CheckTimeout(scheduledTimeout));
        trigger(scheduledTimeout, timer);
    }

    private Handler<Start> startHandler = new Handler<Start>()
    {
        @Override
        public void handle(Start start) {
            startTimer(period);
        }
    };


    private Handler<CheckTimeout> checkTimeoutHandler = new Handler<CheckTimeout>() {
        @Override
        public void handle(CheckTimeout checkTimeout) {
            for(NetAddress address : alive) {
                if(suspected.contains(address)) {
                    period += delta;
                    break;
                }
            }
            seqnum ++;
            for(NetAddress p : topology) {
                if(!alive.contains(p) && !suspected.contains(p)) {
                    suspected.add(p);
                    trigger(new Suspect(p), epfd);
                }
                else if(alive.contains(p) && suspected.contains(p)){
                    suspected.remove(p);
                    trigger(new Restore(p), epfd);
                }
                trigger(new Pp2pSend(p, new HeartbeatRequest(self, seqnum)), pLink);
            }
            alive.clear();
            startTimer(period);
        }
    };

    private Handler<HeartbeatRequest> heartbeatReqHandler = new Handler<HeartbeatRequest>() {
        @Override
        public void handle(HeartbeatRequest heartbeatRequest) {
            trigger(new Pp2pSend(heartbeatRequest.getSource(), new HeartbeatReply(self, heartbeatRequest.getSeqnum())),pLink);
        }
    };

    private Handler<HeartbeatReply> heartbeatRepHandler = new Handler<HeartbeatReply>() {
        @Override
        public void handle(HeartbeatReply heartbeatReply) {
            if(heartbeatReply.getSeqnum() == seqnum || suspected.contains(heartbeatReply.getSource())) {
                alive.add(heartbeatReply.getSource());
            }
        }
    };


}
