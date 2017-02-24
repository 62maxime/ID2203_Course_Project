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
package se.kth.id2203.bootstrapping.component;

import org.slf4j.LoggerFactory;
import se.kth.id2203.bootstrapping.event.Boot;
import se.kth.id2203.bootstrapping.event.Booted;
import se.kth.id2203.bootstrapping.event.CheckIn;
import se.kth.id2203.bootstrapping.event.Ready;
import se.kth.id2203.bootstrapping.port.Bootstrapping;
import se.kth.id2203.epfd.component.EpfdInit;
import se.kth.id2203.epfd.event.ListenTo;
import se.kth.id2203.epfd.event.Reset;
import se.kth.id2203.epfd.event.Suspect;
import se.kth.id2203.epfd.port.EventuallyPerfectFailureDetector;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.CancelPeriodicTimeout;
import se.sics.kompics.timer.SchedulePeriodicTimeout;
import se.sics.kompics.timer.Timer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Lars Kroll <lkroll@kth.se>
 */
public class BootstrapClient extends ComponentDefinition {

    public static enum State {

        WAITING, STARTED;
    }

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(BootstrapClient.class);
    //******* Ports ******
    final Negative<Bootstrapping> bootstrap = provides(Bootstrapping.class);
    final Positive<Timer> timer = requires(Timer.class);
    final Positive<Network> net = requires(Network.class);
    final Positive<EventuallyPerfectFailureDetector> epfd = requires(EventuallyPerfectFailureDetector.class);
    //******* Fields ******
    private final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private final NetAddress server = config().getValue("id2203.project.bootstrap-address", NetAddress.class);

    private State state = State.WAITING;

    private UUID timeoutId;

    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            LOG.debug("Starting bootstrap client on {}", self);
            long timeout = (config().getValue("id2203.project.keepAlivePeriod", Long.class) * 2);
            SchedulePeriodicTimeout spt = new SchedulePeriodicTimeout(timeout, timeout);
            spt.setTimeoutEvent(new BSTimeout(spt));
            trigger(spt, timer);
            timeoutId = spt.getTimeoutEvent().getTimeoutId();
            Set<NetAddress> netAddresses = new HashSet<>();
            netAddresses.add(server);
            trigger(new ListenTo(netAddresses), epfd);
        }
    };
    protected final Handler<BSTimeout> timeoutHandler = new Handler<BSTimeout>() {

        @Override
        public void handle(BSTimeout e) {

            if (state == State.WAITING) {
                trigger(new Message(self, server, CheckIn.event), net);
            } else if (state == State.STARTED) {
                trigger(new Message(self, server, Ready.event), net);
                suicide();
            }
        }
    };

    protected final ClassMatchedHandler<Boot, Message> bootHandler = new ClassMatchedHandler<Boot, Message>() {

        @Override
        public void handle(Boot content, Message context) {
            if (state == State.WAITING) {
                LOG.info("{} Booting up.", self);
                trigger(new Booted(content.assignment), bootstrap);
                trigger(new CancelPeriodicTimeout(timeoutId), timer);
                trigger(new Message(self, server, Ready.event), net);
                state = State.STARTED;
            }
        }
    };

    protected final Handler<Suspect> suspectHandler = new Handler<Suspect>() {
        @Override
        public void handle(Suspect suspect) {
            // Bootstrap server is suspected to be dead => suicide
            LOG.debug("Bootstrap server" + server.toString() + " is suspected.");
            trigger(new Reset(new EpfdInit(self, 1000, 4000)), epfd);
            suicide();
        }
    };


    @Override
    public void tearDown() {
        trigger(new CancelPeriodicTimeout(timeoutId), timer);
    }

    {
        subscribe(startHandler, control);
        subscribe(timeoutHandler, timer);
        subscribe(bootHandler, net);
        subscribe(suspectHandler, epfd);
    }
}
