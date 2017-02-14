package se.kth.id2203.simulation.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;


/**
 * Created by YannL on 13/02/2017.
 */
public class Sender extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(Sender.class);
    //******* Ports ******
    protected final Positive<BebPort> beb = requires(BebPort.class);
    //******* Fields ******
    private final NetAddress self;

    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {
        @Override
        public void handle(Start start) {
            LOG.info("[Sender] Start sender " + self);
            BebRequest bebRequest = new BebRequest(null);
            trigger(bebRequest, beb);
        }
    };



    protected final Handler<BebDeliver> bebDeliverHandler = new Handler<BebDeliver>() {
        @Override
        public void handle(BebDeliver bebDeliver) {
            LOG.info("[Sender] BebDeliver from " + bebDeliver.source + " received by node " + self.toString());
        }
    };


    public Sender(Init init) {
        this.self = init.self;

        subscribe(startHandler, control);
        subscribe(bebDeliverHandler, beb);
    }

    public static class Init extends se.sics.kompics.Init<Sender> {
        public final NetAddress self;

        public Init(NetAddress self) {
            this.self = self;
        }
    }
}
