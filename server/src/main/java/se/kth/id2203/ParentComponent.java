package se.kth.id2203;

import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.BootstrapClient;
import se.kth.id2203.bootstrapping.BootstrapServer;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.epfd.component.Epfd;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.kvstore.KVEntry;
import se.kth.id2203.kvstore.KVService;
import se.kth.id2203.kvstore.KVServiceInit;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.overlay.VSOverlayManager;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.HashMap;

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

    {
        HashMap<Integer, KVEntry> store = new HashMap<>();
        store.put(Integer.MAX_VALUE - 1, new KVEntry(Integer.MAX_VALUE - 1, 42));
        store.put(Integer.MAX_VALUE/2, new KVEntry(Integer.MAX_VALUE/2, 41));
        store.put("test0".hashCode(), new KVEntry("test0".hashCode(), 41));
        store.put("test1".hashCode(), new KVEntry("test1".hashCode(), 41));
        store.put("test2".hashCode(), new KVEntry("test2".hashCode(), 41));
        kv = create(KVService.class, new KVServiceInit(store));

        LOG.debug("IP {} Port {}", self.getIp(), self.getPort());
        EpfdInit init = new EpfdInit(self,(long) 1000, 4000);
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


    }
}
