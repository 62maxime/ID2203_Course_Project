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
package se.kth.id2203.kvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.beb.event.BebDeliver;
import se.kth.id2203.beb.port.BebPort;
import se.kth.id2203.kvstore.OpResponse.Code;
import se.kth.id2203.leaderdetection.event.Trust;
import se.kth.id2203.leaderdetection.port.MonarchicalEventualLeaderDetection;
import se.kth.id2203.multipaxos.event.AscAbort;
import se.kth.id2203.multipaxos.event.AscDecide;
import se.kth.id2203.multipaxos.event.AscPropose;
import se.kth.id2203.multipaxos.port.AscPort;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.sics.kompics.ClassMatchedHandler;
import se.sics.kompics.ComponentDefinition;
import se.sics.kompics.Handler;
import se.sics.kompics.Positive;
import se.sics.kompics.network.Network;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Lars Kroll <lkroll@kth.se>
 */
public class KVService extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(KVService.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Routing> route = requires(Routing.class); // TODO remove if not used
    protected final Positive<MonarchicalEventualLeaderDetection> meld =
            requires(MonarchicalEventualLeaderDetection.class);
    protected final Positive<AscPort> asc = requires(AscPort.class);
    protected final Positive<BebPort> beb = requires(BebPort.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private NetAddress leader = null;
    private HashMap<Integer, KVEntry> store;

    //******* Constructor ******
    public KVService(KVServiceInit init) {
        this.store = new HashMap<>(init.getStore());
    }

    //******* Handlers ******
    protected final ClassMatchedHandler<Operation, BebDeliver> opHandler = new ClassMatchedHandler<Operation, BebDeliver>() {

        @Override
        public void handle(Operation content, BebDeliver context) {
            LOG.info("Got operation {}! Now implement me please :)", content);
            trigger(new Message(self, context.source, (new OpResponse(content.id, Code.NOT_IMPLEMENTED))), net);

        }

    };

    private void proposeOperation(Operation content) {
        LOG.debug("Got {}", content);
        if (leader.equals(self)) {
            LOG.debug("{} is the leader, propose {}", self, content);
            trigger(new AscPropose(content), asc);
        } else {
            LOG.debug("{} is not the leader, drop {}", self, content);
            //trigger(new Message(self, leader, content), net);
        }
    }

    protected final ClassMatchedHandler<GetRequest, BebDeliver> getHandler = new ClassMatchedHandler<GetRequest, BebDeliver>() {

        @Override
        public void handle(GetRequest content, BebDeliver context) {
            proposeOperation(content);
        }

    };
    protected final ClassMatchedHandler<PutRequest, BebDeliver> putHandler = new ClassMatchedHandler<PutRequest, BebDeliver>() {

        @Override
        public void handle(PutRequest content, BebDeliver context) {
            proposeOperation(content);
        }

    };
    protected final ClassMatchedHandler<CasRequest, BebDeliver> casHandler = new ClassMatchedHandler<CasRequest, BebDeliver>() {

        @Override
        public void handle(CasRequest content, BebDeliver context) {
            proposeOperation(content);
        }

    };
    protected final Handler<Trust> trustHandler = new Handler<Trust>() {
        @Override
        public void handle(Trust trust) {
            leader = trust.getLeader();
            LOG.debug("From {}, Got a new leader {}", self, leader);
        }
    };
    protected final ClassMatchedHandler<GetRequest, AscDecide> getRequestClassMatchedHandler = new ClassMatchedHandler<GetRequest, AscDecide>() {
        @Override
        public void handle(GetRequest getRequest, AscDecide ascDecide) {
            LOG.debug("Decide {}", getRequest);
            Integer key = getRequest.key.hashCode();
            UUID uuid = getRequest.id;
            KVEntry value = store.get(key);
            NetAddress address = getRequest.getSource();
            LOG.debug("Result {} {} {}", uuid, value, address);
            if (address == null || !(leader.equals(self))) {
                return;
            }

            if (value == null) {
                trigger(new Message(self, address, new GetResponse(uuid, Code.NOT_FOUND, value)), net);
            } else {
                trigger(new Message(self, address, new GetResponse(uuid, Code.OK, value)), net);
            }
        }
    };
    protected final ClassMatchedHandler<PutRequest, AscDecide> putRequestClassMatchedHandler = new ClassMatchedHandler<PutRequest, AscDecide>() {
        @Override
        public void handle(PutRequest putRequest, AscDecide ascDecide) {
            LOG.debug("Decide {}", putRequest);
            Integer key = putRequest.key.hashCode();
            UUID uuid = putRequest.id;
            NetAddress address = putRequest.getSource();
            if (address == null) {
                return;
            }
            store.put(key, putRequest.getValue());
            if (leader.equals(self)) {
                trigger(new Message(self, address, new PutResponse(uuid, Code.OK)), net);
            }
        }
    };
    protected final ClassMatchedHandler<CasRequest, AscDecide> casRequestClassMatchedHandler = new ClassMatchedHandler<CasRequest, AscDecide>() {
        @Override
        public void handle(CasRequest casRequest, AscDecide ascDecide) {
            LOG.debug("Decide {}", casRequest);
            Integer key = casRequest.key.hashCode();
            UUID uuid = casRequest.id;
            NetAddress address = casRequest.getSource();
            if (address == null) {
                return;
            }
            KVEntry value = store.get(key);
            boolean success = false;
            if (value == null) {
                trigger(new Message(self, address, new CasResponse(uuid, Code.NOT_FOUND, success)), net);
            } else {
                if (value.getValue().equals(casRequest.getOldValue().getValue())) {
                    store.put(key, casRequest.getNewValue());
                    success = true;
                }
                if (leader.equals(self)) {
                    trigger(new Message(self, address, new CasResponse(uuid, Code.OK, success)), net);
                }
            }
        }
    };

    protected final Handler<AscAbort> abortHandler = new Handler<AscAbort>() {
        @Override
        public void handle(AscAbort ascAbort) {
            // TODO Handle when a command is aborted
            LOG.debug("Abort {}", ascAbort.getOperation());
            Operation operation = ascAbort.getOperation();
            proposeOperation(operation);
        }
    };


    {
        subscribe(opHandler, beb);
        subscribe(getHandler, beb);
        subscribe(putHandler, beb);
        subscribe(casHandler, beb);
        subscribe(trustHandler, meld);
        subscribe(getRequestClassMatchedHandler, asc);
        subscribe(putRequestClassMatchedHandler, asc);
        subscribe(casRequestClassMatchedHandler, asc);
        subscribe(abortHandler, asc);
    }

}
