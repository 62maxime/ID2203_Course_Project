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
import se.kth.id2203.kvstore.KVService;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.overlay.VSOverlayManager;
import se.sics.kompics.Channel;
import se.sics.kompics.Component;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Init;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Address;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;
import sun.rmi.runtime.Log;

import java.util.HashSet;
import java.util.Set;

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
    protected final Component kv = create(KVService.class, Init.NONE);

    protected final Component epfd ;
    protected final Component boot;

    {
        LOG.debug("IP {} Port {}", self.getIp(), self.getPort());
        EpfdInit init = new EpfdInit(self,(long) 10,(long) 40);
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
    }
}
