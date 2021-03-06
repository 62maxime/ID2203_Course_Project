/*
 * The MIT License
 *
 * Copyright 2017 Lars Kroll <lkroll@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package se.kth.id2203.overlay;

import com.larskroll.common.J6;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebRequest;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.bootstrapping.event.Booted;
import se.kth.id2203.bootstrapping.event.GetInitialAssignments;
import se.kth.id2203.bootstrapping.event.InitialAssignments;
import se.kth.id2203.bootstrapping.port.Bootstrapping;
import se.kth.id2203.common.event.GroupTopology;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.event.ListenTo;
import se.kth.id2203.epfd.event.Reset;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.multipaxos.event.MPgroup;
import se.kth.id2203.multipaxos.port.AscPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.Collection;

/**
 * The V(ery)S(imple)OverlayManager.
 * <p>
 * Keeps all nodes in a single partition in one replication group.
 * <p>
 * Note: This implementation does not fulfill the project task. You have to
 * support multiple partitions!
 * <p>
 *
 * @author Lars Kroll <lkroll@kth.se>
 */
public class VSOverlayManager extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(VSOverlayManager.class);
    //******* Ports ******
    protected final Negative<Routing> route = provides(Routing.class);
    protected final Positive<Bootstrapping> boot = requires(Bootstrapping.class);
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    protected final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    protected final Positive<AscPort> asc = requires(AscPort.class);
    protected final Positive<BebPort> beb = requires(BebPort.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private LookupTable lut = null;
    private ReplicationGroup replicationGroup;
    private boolean booted = false;
    //******* Handlers ******
    protected final Handler<GetInitialAssignments> initialAssignmentHandler = new Handler<GetInitialAssignments>() {

        @Override
        public void handle(GetInitialAssignments event) {
            int partitionGroupNumber = config().getValue("id2203.project.partitionGroupNumber", Integer.class);
            int replicationDelta = config().getValue("id2203.project.replicationDelta", Integer.class);
            LOG.info("Generating LookupTable...");
            lut = LookupTable.generate(event.nodes, partitionGroupNumber, replicationDelta);
            LOG.debug("Generated assignments:\n{}", lut);
            initEpfd();
            trigger(new InitialAssignments(lut), boot);
        }
    };
    protected final Handler<Booted> bootHandler = new Handler<Booted>() {

        @Override
        public void handle(Booted event) {
            if (event.assignment instanceof LookupTable) {
                LOG.info("Got NodeAssignment, overlay ready.");
                lut = (LookupTable) event.assignment;
                initEpfd();
                trigger(new MPgroup(lut.getKey(self)), asc);
            } else {
                LOG.error("Got invalid NodeAssignment type. Expected: LookupTable; Got: {}", event.assignment.getClass());
            }
        }
    };
    protected final ClassMatchedHandler<RouteMsg, Message> routeHandler = new ClassMatchedHandler<RouteMsg, Message>() {

        @Override
        public void handle(RouteMsg content, Message context) {
            Collection<NetAddress> partition = lut.lookup(content.key);
            LOG.info("Forwarding message for key {} to {}", content.key, partition);
            trigger(new BebRequest(content.msg, partition), beb);
            /*NetAddress target = J6.randomElement(partition);
            trigger(new Message(context.getSource(), target, content.msg), net);*/

        }
    };
    // TODO remove this handler if not used in the future (KVService currently does not send RouteMsg to the Overlay)
    protected final Handler<RouteMsg> localRouteHandler = new Handler<RouteMsg>() {

        @Override
        public void handle(RouteMsg event) {
            Collection<NetAddress> partition = lut.lookup(event.key);
            //trigger(new BebRequest(event.msg, partition), beb);
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

    private void initEpfd() {
        Integer delta = config().getValue("id2203.project.EpfdDelta", Integer.class);
        Integer period = config().getValue("id2203.project.EpfdPeriod", Integer.class);
        replicationGroup = lut.getKey(self);
        trigger(new Reset(new EpfdInit(self, delta, period)), epfd);
        trigger(new ListenTo(replicationGroup.getNodes()), epfd);
        trigger(new Message(self, self, new GroupTopology(replicationGroup)), net);
        booted = true;
    }

    {
        subscribe(initialAssignmentHandler, boot);
        subscribe(bootHandler, boot);
        subscribe(routeHandler, net);
        subscribe(localRouteHandler, route);
        subscribe(connectHandler, net);
    }
}
