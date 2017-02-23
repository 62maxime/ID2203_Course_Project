package se.kth.id2203;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.component.BebInit;
import se.kth.id2203.beb.component.BestEffortBroadcast;
import se.kth.id2203.bootstrapping.component.BootstrapClient;
import se.kth.id2203.bootstrapping.component.BootstrapServer;
import se.kth.id2203.bootstrapping.port.Bootstrapping;
import se.kth.id2203.epfd.component.Epfd;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.kvstore.KVEntry;
import se.kth.id2203.kvstore.KVService;
import se.kth.id2203.kvstore.KVServiceInit;
import se.kth.id2203.leaderdetection.component.MELD;
import se.kth.id2203.leaderdetection.component.MELDInit;
import se.kth.id2203.leaderdetection.port.MonarchicalEventualLeaderDetection;
import se.kth.id2203.multipaxos.component.MultiPaxos;
import se.kth.id2203.multipaxos.component.MultiPaxosInit;
import se.kth.id2203.multipaxos.port.AscPort;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.overlay.VSOverlayManager;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.HashMap;
import java.util.HashSet;

public class ParentComponent
        extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(ParentComponent.class);
    //******* Init *******
    NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //******* Children ******
    protected final Component overlay = create(VSOverlayManager.class, Init.NONE);
    protected final Component kv;
    protected final Component epfd;
    protected final Component boot;
    protected final Component beb = create(BestEffortBroadcast.class, new BebInit(self, new HashSet<NetAddress>()));
    protected final Component meld = create(MELD.class, new MELDInit(new HashSet<NetAddress>()));
    protected final Component asc = create(MultiPaxos.class, new MultiPaxosInit(self, self.hashCode()));

    {
        HashMap<Integer, KVEntry> store = new HashMap<>();
        store.put(Integer.MAX_VALUE - 1, new KVEntry(Integer.MAX_VALUE - 1, 42));
        store.put(Integer.MAX_VALUE / 2, new KVEntry(Integer.MAX_VALUE / 2, 37));
        store.put("test0".hashCode(), new KVEntry("test0".hashCode(), 41));
        store.put("test1".hashCode(), new KVEntry("test1".hashCode(), 40));
        store.put("test2".hashCode(), new KVEntry("test2".hashCode(), 39));
        kv = create(KVService.class, new KVServiceInit(store));

        LOG.debug("IP {} Port {}", self.getIp(), self.getPort());
        Integer delta = config().getValue("id2203.project.EpfdDelta", Integer.class);
        Integer period = config().getValue("id2203.project.EpfdPeriod", Integer.class);
        EpfdInit init = new EpfdInit(self, delta, period);
        epfd = create(Epfd.class, init);

        Optional<NetAddress> serverO = config().readValue("id2203.project.bootstrap-address", NetAddress.class);
        if (serverO.isPresent()) { // start in client mode
            boot = create(BootstrapClient.class, Init.NONE);
        } else { // start in server mode
            boot = create(BootstrapServer.class, Init.NONE);
        }
        connect(timer, boot.getNegative(Timer.class), Channel.TWO_WAY);
        connect(net, boot.getNegative(Network.class), Channel.TWO_WAY);
        // Overlay
        connect(boot.getPositive(Bootstrapping.class), overlay.getNegative(Bootstrapping.class), Channel.TWO_WAY);
        connect(net, overlay.getNegative(Network.class), Channel.TWO_WAY);
        // KV
        connect(overlay.getPositive(Routing.class), kv.getNegative(Routing.class), Channel.TWO_WAY);
        connect(net, kv.getNegative(Network.class), Channel.TWO_WAY);
        // EPFD
        connect(epfd.getPositive(EventuallyPerfectFailureDetector.class),
                boot.getNegative(EventuallyPerfectFailureDetector.class), Channel.TWO_WAY);
        connect(epfd.getPositive(EventuallyPerfectFailureDetector.class),
                overlay.getNegative(EventuallyPerfectFailureDetector.class), Channel.TWO_WAY);
        connect(net, epfd.getNegative(Network.class), Channel.TWO_WAY);
        connect(timer, epfd.getNegative(Timer.class), Channel.TWO_WAY);
        // BEB
        connect(net, beb.getNegative(Network.class), Channel.TWO_WAY);
        // MELD
        connect(epfd.getPositive(EventuallyPerfectFailureDetector.class),
                meld.getNegative(EventuallyPerfectFailureDetector.class), Channel.TWO_WAY);
        connect(net, meld.getNegative(Network.class), Channel.TWO_WAY);
        connect(meld.getPositive(MonarchicalEventualLeaderDetection.class),
                kv.getNegative(MonarchicalEventualLeaderDetection.class), Channel.TWO_WAY);
        //ASC
        connect(net, asc.getNegative(Network.class), Channel.TWO_WAY);
        connect(asc.getPositive(AscPort.class), kv.getNegative(AscPort.class), Channel.TWO_WAY);


    }

    Handler<Kill> killHandler = new Handler<Kill>() {
        @Override
        public void handle(Kill kill) {
            LOG.debug("Killed");
            destroy(overlay);
            destroy(kv);
            destroy(epfd);
            destroy(boot);
            destroy(beb);
        }
    };

    {
        subscribe(killHandler, control);
    }
}
