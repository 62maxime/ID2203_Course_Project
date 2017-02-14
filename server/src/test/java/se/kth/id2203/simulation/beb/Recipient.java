package se.kth.id2203.simulation.beb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.Start;

/**
 * Created by YannL on 13/02/2017.
 */
public class Recipient extends ComponentDefinition {
    final static Logger LOG = LoggerFactory.getLogger(Recipient.class);
    //******* Ports ******
    protected final Positive<BebPort> beb = requires(BebPort.class);
    //******* Fields ******
    private final NetAddress self;
    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>(){
        public void handle(Start event) {
            LOG.info("[Recipient] Start Recipient " + self);
        }
    };

    protected final Handler<BebDeliver> bebDeliverHandler = new Handler<BebDeliver>() {

        @Override
        public void handle(BebDeliver bebDeliver) {
            LOG.info("[Recipient] BebDeliver from " + bebDeliver.source + " received by node " + self.toString());
        }
    };

    public Recipient(Init init) {
        this.self = init.self;

        subscribe(bebDeliverHandler, beb);
        subscribe(startHandler, control);
    }

    public static class Init extends se.sics.kompics.Init<Recipient> {
        public final NetAddress self;

        public Init(NetAddress self) {
            this.self = self;
        }
    }
}
