package se.kth.id2203.sharedmemory.component;

import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.kvstore.KVEntry;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.sharedmemory.event.*;
import se.kth.id2203.sharedmemory.port.ReadImposeWriteConsult;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.HashMap;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class RIWC extends ComponentDefinition {

    //******* Ports ******
    private final Negative<ReadImposeWriteConsult> nnar = provides(ReadImposeWriteConsult.class);
    private final Positive<Network> pLink = requires(Network.class);
    private final Positive<BebPort> beb = requires(BebPort.class);
    //****** Fields ******
    private NetAddress self;
    private int n;
    private int selfRank;
    private Triplet selfTriplet;
    protected final ClassMatchedHandler<Read, BebDeliver> readBebDeliverHandler = new ClassMatchedHandler<Read, BebDeliver>() {
        @Override
        public void handle(Read read, BebDeliver bebDeliver) {
            trigger(new Message(self, bebDeliver.source, new Value(read.getRid(), selfTriplet.getTs(),
                    selfTriplet.getWr(), selfTriplet.getValue())), pLink);
        }
    };
    protected final ClassMatchedHandler<Write, BebDeliver> writeBebDeliverHandler = new ClassMatchedHandler<Write, BebDeliver>() {
        @Override
        public void handle(Write write, BebDeliver bebDeliver) {
            Triplet triplet = new Triplet(write.getTs(), write.getWr(), write.getWriteValue());
            if (selfTriplet.isLowerOrEqualThan(triplet)) {
                selfTriplet.setTs(write.getTs());
                selfTriplet.setWr(write.getWr());
                selfTriplet.setValue(write.getWriteValue());
            }
            trigger(new Message(self, bebDeliver.source, new Ack(write.getRid())), pLink);
        }
    };
    private int acks = 0;
    private KVEntry readVal = null;
    private KVEntry writeVal = null;
    private int rid = 0;
    private HashMap<NetAddress, Triplet> readList;
    protected final Handler<AR_Write_Request> writeRequestHandler = new Handler<AR_Write_Request>() {
        @Override
        public void handle(AR_Write_Request ar_write_request) {
            rid = rid + 1;
            writeVal = ar_write_request.getValue();
            acks = 0;
            readList.clear();
            trigger(new BebRequest(new Read(rid)), beb);
        }
    };
    private boolean reading = false;
    //****** Handlers ******
    protected final Handler<AR_Read_Request> readRequestHandler = new Handler<AR_Read_Request>() {
        @Override
        public void handle(AR_Read_Request ar_read_request) {
            rid = rid + 1;
            acks = 0;
            readList.clear();
            reading = true;
            trigger(new BebRequest(new Read(rid)), beb);
        }
    };
    protected final ClassMatchedHandler<Value, Message> valueMessageHandler = new ClassMatchedHandler<Value, Message>() {
        @Override
        public void handle(Value value, Message message) {
            if (value.getRid() == rid) {
                readList.put(message.getSource(), new Triplet(value.getTs(), value.getWr(), value.getValue()));
                if (readList.keySet().size() > (n / 2)) {
                    NetAddress src = readList.keySet().iterator().next();
                    Triplet triplet = readList.get(src);

                    for (Triplet pts :
                            readList.values()) {
                        if (triplet.isLowerOrEqualThan(pts)) {
                            triplet = new Triplet(pts);
                        }
                    }
                    readVal = triplet.getValue();
                    readList.clear();
                    KVEntry bCastVal;
                    if (reading) {
                        bCastVal = triplet.getValue();
                    } else {
                        triplet.setWr(selfRank);
                        triplet.setTs(triplet.getTs() + 1);
                        bCastVal = writeVal;
                    }

                    trigger(new BebRequest(new Write(rid, triplet.getTs(), triplet.getWr(), bCastVal)), beb);
                }

            }
        }
    };
    protected final ClassMatchedHandler<Ack, Message> ackMessageHandler = new ClassMatchedHandler<Ack, Message>() {
        @Override
        public void handle(Ack ack, Message message) {
            if (ack.getRid() == rid) {
                acks += 1;
                if (acks > n / 2) {
                    acks = 0;
                    if (reading) {
                        reading = false;
                        trigger(new AR_Read_Response(readVal), nnar);
                    } else {
                        trigger(new AR_Write_Response(), nnar);
                    }
                }
            }
        }
    };

    {
        subscribe(readRequestHandler, nnar);
        subscribe(writeRequestHandler, nnar);
        subscribe(writeBebDeliverHandler, beb);
        subscribe(readBebDeliverHandler, beb);
        subscribe(valueMessageHandler, pLink);
        subscribe(ackMessageHandler, pLink);
    }

    public RIWC(RIWCInit init) {
        this.self = init.getSelf();
        this.n = init.getN();
        this.selfRank = init.getSelfRank();
        readList = new HashMap<>();
        this.selfTriplet = new Triplet(0, 0, null);
    }

    private class Triplet {
        private int ts;
        private int wr;
        private KVEntry value;

        private Triplet(int ts, int wr, KVEntry value) {
            this.ts = ts;
            this.wr = wr;
            this.value = value;
        }

        private Triplet(Triplet pts) {
            this.ts = pts.getTs();
            this.wr = pts.getWr();
            this.value = pts.getValue();
        }

        private int getTs() {
            return ts;
        }

        private void setTs(int ts) {
            this.ts = ts;
        }

        private int getWr() {
            return wr;
        }

        private void setWr(int wr) {
            this.wr = wr;
        }

        private KVEntry getValue() {
            return value;
        }

        private void setValue(KVEntry value) {
            this.value = value;
        }

        private boolean isLowerOrEqualThan(Triplet triplet) {
            return (this.ts <= triplet.getTs()) || ((this.ts <= triplet.getTs()) && (this.wr <= triplet.getWr()));
        }
    }


}
