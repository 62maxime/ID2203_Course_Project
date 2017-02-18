package se.kth.id2203.simulation.group;

import com.larskroll.common.J6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.Booted;
import se.kth.id2203.bootstrapping.Bootstrapping;
import se.kth.id2203.bootstrapping.GetInitialAssignments;
import se.kth.id2203.bootstrapping.InitialAssignments;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.*;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.Collection;

/**
 * Created by ralambom on 16/02/17.
 */
public class ScenarioOverlay extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(ScenarioOverlay.class);
    //******* Ports ******
    protected final Negative<Routing> route = provides(Routing.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private LookupTable lut = null;
    private ReplicationGroup replicationGroup;
    //******* Handlers ******
    protected final Handler<GetInitialAssignments> initialAssignmentHandler = new Handler<GetInitialAssignments>() {

        @Override
        public void handle(GetInitialAssignments event) {
            int partitionGroupNumber = config().getValue("id2203.project.partitionGroupNumber", Integer.class);
            int replicationDelta = config().getValue("id2203.project.replicationDelta", Integer.class);
            LOG.info("Generating LookupTable...");
            LookupTable lut = LookupTable.generate(event.nodes, partitionGroupNumber, replicationDelta);
            LOG.debug("Generated assignments:\n{}", lut);
            replicationGroup = lut.getKey(self);
            trigger(new InitialAssignments(lut), boot);
        }
    };

    protected final Handler<Booted> bootHandler = new Handler<Booted>() {

        @Override
        public void handle(Booted event) {
            if (event.assignment instanceof LookupTable) {
                LOG.info("Got NodeAssignment, overlay ready.");
                lut = (LookupTable) event.assignment;
                replicationGroup = lut.getKey(self);
                SimulationResult.setResultTable(self, lut);
                LOG.info("[Node] Address: " + self + "; ReplicationGroup: " + replicationGroup.getNodes().toString());
            } else {
                LOG.error("Got invalid NodeAssignment type. Expected: LookupTable; Got: {}", event.assignment.getClass());
            }
        }
    };
    protected final ClassMatchedHandler<RouteMsg, Message> routeHandler = new ClassMatchedHandler<RouteMsg, Message>() {

        @Override
        public void handle(RouteMsg content, Message context) {
            Collection<NetAddress> partition = lut.lookup(content.key);
            NetAddress target = J6.randomElement(partition);
            LOG.info("Forwarding message for key {} to {}", content.key, target);
            trigger(new Message(context.getSource(), target, content.msg), net);

        }
    };
    protected final Handler<RouteMsg> localRouteHandler = new Handler<RouteMsg>() {

        @Override
        public void handle(RouteMsg event) {
            Collection<NetAddress> partition = lut.lookup(event.key);
            NetAddress target = J6.randomElement(partition);
            LOG.info("Routing message for key {} to {}", event.key, target);
            trigger(new Message(self, target, event.msg), net);

        }
    };
    protected final ClassMatchedHandler<Connect, Message> connectHandler = new ClassMatchedHandler<Connect, Message>() {

        @Override
        public void handle(Connect content, Message context) {
            if (lut != null) {
                LOG.debug("Accepting connection request from {}", context.getSource());
                int size = lut.getNodes().size();
                trigger(new Message(self, context.getSource(), content.ack(size)), net);
            } else {
                LOG.info("Rejecting connection request from {}, as system is not ready, yet.", context.getSource());
            }
        }
    };

    {
        subscribe(initialAssignmentHandler, boot);
        subscribe(bootHandler, boot);
        subscribe(routeHandler, net);
        subscribe(localRouteHandler, route);
        subscribe(connectHandler, net);
    }

}
