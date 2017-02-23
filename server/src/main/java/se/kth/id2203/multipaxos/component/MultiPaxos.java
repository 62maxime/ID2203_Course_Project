package se.kth.id2203.multipaxos.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.common.port.GroupTopology;
import se.kth.id2203.kvstore.Operation;
import se.kth.id2203.multipaxos.event.*;
import se.kth.id2203.multipaxos.port.AscPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.ReplicationGroup;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ralambom on 23/02/17.
 */
public class MultiPaxos extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(MultiPaxos.class);

    //******* Ports ******
    private Negative<AscPort> asc = provides(AscPort.class);
    private Positive<Network> fpl = requires(Network.class);

    //******* Fields ******
    private int t; //logical clock
    private int prepts; //prepared timestamp
    private int ats; //acceptor timestamp
    private ArrayList<Operation> av; //accepted sequence
    private int al; //length of decided sequence
    private int pts; //proposer timestamp
    private ArrayList<Operation> pv; //proposed sequence
    private int pl;  //length of learned sequence
    private ArrayList<Operation> proposedValues; //proposer: values proposed while preparing
    private HashMap<NetAddress, ReadItem> readlist;
    private HashMap<NetAddress, Integer> accepted; //proposer’s knowledge about length of acceptor’s longest accepted seq
    private HashMap<NetAddress, Integer> decided; //proposer’s knowledge about length of acceptor’s longest decided seq

    //configuration parameters
    private NetAddress self;
    private ReplicationGroup replicationGroup;
    private int selfRank;

    // Current Operation
    private Operation currentOperation;

    //******* Init *******
    public MultiPaxos(MultiPaxosInit init) {

        this.self = init.getSelf();
        this.selfRank = init.getSelfRank();

        this.t = 0;
        this.prepts = 0;

        this.ats = 0;
        this.av = new ArrayList<>();
        this.al = 0;

        this.pts = 0;
        this.pv = new ArrayList<>();
        this.pl = 0;

        this.proposedValues = new ArrayList<>();
        this.readlist = new HashMap<>();
        this.accepted = new HashMap<>();
        this.decided = new HashMap<>();
        if (replicationGroup != null) {
            for (NetAddress add : replicationGroup.getNodes()) {
                this.accepted.put(add, 0);
                this.decided.put(add, 0);
            }
        }

        //subscriptions to handlers
        subscribe(mPgroupHandler, asc);
        subscribe(proposeHandler, asc);
        subscribe(prepareHandler, fpl);
        subscribe(nackHandler, fpl);
        subscribe(prepareAckHandler, fpl);
        subscribe(acceptHandler, fpl);
        subscribe(acceptAckHandler, fpl);
        subscribe(decideHandler, fpl);
        subscribe(topologyMessageMatchedHandler, fpl);
    }


    //******* Handlers ******

    // FETCH REPLICATION GROUP

    private Handler<MPgroup> mPgroupHandler = new Handler<MPgroup>() {
        @Override
        public void handle(MPgroup mPgroup) {
            replicationGroup = mPgroup.getGroup();
            for (NetAddress add : replicationGroup.getNodes()) {
                accepted.put(add, 0);
                decided.put(add, 0);
            }
        }
    };

    // PREPARE PHASE

    private Handler<AscPropose> proposeHandler = new Handler<AscPropose>() {
        @Override
        public void handle(AscPropose ascPropose) {
            t++;
            currentOperation = ascPropose.getOperation();
            if (pts == 0) {
                pts = t * replicationGroup.getSize() + selfRank;
                pv = prefix(av, al);
                pl = 0;
                proposedValues.clear();
                proposedValues.add(ascPropose.getOperation());
                readlist.clear();
                accepted.clear();
                decided.clear();
                for (NetAddress add : replicationGroup.getNodes()) {
                    accepted.put(add, 0);
                    decided.put(add, 0);
                }
                for (NetAddress add : replicationGroup.getNodes()) {
                    trigger(new Prepare(self, add, pts, al, t), fpl);
                }
            } else if (readlist.size() <= replicationGroup.getSize() / 2) {
                proposedValues.add(ascPropose.getOperation());
            } else if (!pv.contains(ascPropose.getOperation())) {
                pv.add(ascPropose.getOperation());
                for (NetAddress add : replicationGroup.getNodes()) {
                    if (readlist.containsKey(add)) {
                        ArrayList<Operation> v = new ArrayList<>();
                        v.add(ascPropose.getOperation());
                        trigger(new Accept(self, add, pts, v, pv.size() - 1, t), fpl);
                    }
                }
            }
        }
    };

    private Handler<Prepare> prepareHandler = new Handler<Prepare>() {
        @Override
        public void handle(Prepare prepare) {
            t = Math.max(t, prepare.getT()) + 1;
            if (prepare.getTs() < prepts) {
                trigger(new Nack(self, prepare.getSource(), prepare.getTs(), t), fpl);
            } else {
                prepts = prepare.getTs();
                trigger(new PrepareAck(self, prepare.getSource(), prepare.getTs(), ats, suffix(av, prepare.getL()), al, t), fpl);
            }
        }
    };

    private Handler<Nack> nackHandler = new Handler<Nack>() {
        @Override
        public void handle(Nack nack) {
            t = Math.max(t, nack.getT()) + 1;
            if (nack.getPts() == pts) {
                pts = 0;
                trigger(new AscAbort(currentOperation), asc);
            }
        }
    };

    //ACCEPT PHASE

    private Handler<PrepareAck> prepareAckHandler = new Handler<PrepareAck>() {
        @Override
        public void handle(PrepareAck prepareAck) {
            t = Math.max(t, prepareAck.getT()) + 1;
            if (prepareAck.getPts() == pts) {
                readlist.put(prepareAck.getSource(), new ReadItem(prepareAck.getTs(), prepareAck.getVsuf()));
                decided.put(prepareAck.getSource(), prepareAck.getL());
                if (readlist.size() == replicationGroup.getSize() / 2 + 1) {
                    int tsP = 0;
                    ArrayList<Operation> vsufP = new ArrayList<>();
                    for (Map.Entry<NetAddress, ReadItem> entry : readlist.entrySet()) {
                        int tsPP = entry.getValue().getTs();
                        ArrayList<Operation> vsufPP = entry.getValue().getVsuf();
                        if (tsP < tsPP || (tsP == tsPP && vsufP.size() < vsufPP.size())) {
                            tsP = tsPP;
                            vsufP = vsufPP;
                        }
                    }
                    for (Operation v : proposedValues) {
                        if (!pv.contains(v)) {
                            pv.add(v);
                        }
                    }
                    for (NetAddress add : replicationGroup.getNodes()) {
                        if (readlist.containsKey(add)) {
                            trigger(new Accept(self, add, pts, suffix(pv, decided.get(add)), decided.get(add), t), fpl);
                        }
                    }
                } else if (readlist.size() > replicationGroup.getSize() / 2 + 1) {
                    trigger(new Accept(self, prepareAck.getSource(), pts, suffix(pv, prepareAck.getL()), prepareAck.getL(), t), fpl);
                    if (pl != 0) {
                        trigger(new Decide(self, prepareAck.getSource(), pts, pl, t), fpl);
                    }
                }
            }
        }
    };

    private Handler<Accept> acceptHandler = new Handler<Accept>() {
        @Override
        public void handle(Accept accept) {
            t = Math.max(t, accept.getT());
            if (accept.getTs() != prepts) {
                trigger(new Nack(self, accept.getSource(), accept.getTs(), t), fpl);
            } else {
                ats = accept.getTs();
                if (accept.getOffs() < av.size()) {
                    av = prefix(av, accept.getOffs());
                }
                av.addAll(accept.getVsuf());
                trigger(new AcceptAck(self, accept.getSource(), accept.getTs(), av.size(), t), fpl);
            }
        }
    };

    private Handler<AcceptAck> acceptAckHandler = new Handler<AcceptAck>() {
        @Override
        public void handle(AcceptAck acceptAck) {
            t = Math.max(t, acceptAck.getT()) + 1;
            if (acceptAck.getPts() == pts) {
                accepted.put(acceptAck.getSource(), acceptAck.getL());
                int n = 0;
                for (NetAddress add : replicationGroup.getNodes()) {
                    if (accepted.get(add) >= acceptAck.getL()) {
                        n++;
                    }
                }
                if (pl < acceptAck.getL() && n > replicationGroup.getSize() / 2) {
                    pl = acceptAck.getL();
                    for (NetAddress p : replicationGroup.getNodes()) {
                        if (readlist.containsKey(p)) {
                            trigger(new Decide(self, p, pts, pl, t), fpl);
                        }
                    }
                }
            }
        }
    };

    private Handler<Decide> decideHandler = new Handler<Decide>() {
        @Override
        public void handle(Decide decide) {
            t = Math.max(t, decide.getT());
            if (decide.getTs() == prepts) {
                while (al < decide.getL()) {
                    trigger(new AscDecide(av.get(al)), asc);
                    al++;
                }
            }
        }
    };

    protected ClassMatchedHandler<GroupTopology, Message> topologyMessageMatchedHandler = new ClassMatchedHandler<GroupTopology, Message>() {
        @Override
        public void handle(GroupTopology groupTopology, Message message) {
            LOG.debug("Receive Topology {}", groupTopology.getTopology());
            replicationGroup = groupTopology.getTopology();
            for (NetAddress add : replicationGroup.getNodes()) {
                accepted.clear();
                accepted.put(add, 0);
                decided.clear();
                decided.put(add, 0);
            }
        }
    };


    //********** Functions **********

    private ArrayList<Operation> prefix(ArrayList<Operation> av, int al) {
        ArrayList<Operation> prefix = new ArrayList<Operation>();
        for (int i = 0; i < al; i++) {
            prefix.add(av.get(i));
        }
        return prefix;
    }

    private ArrayList<Operation> suffix(ArrayList<Operation> vsuf, int al) {
        return new ArrayList<Operation>(vsuf.subList(al, vsuf.size()));
    }

}
