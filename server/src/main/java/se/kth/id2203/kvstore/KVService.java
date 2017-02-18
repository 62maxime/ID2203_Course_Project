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
import se.kth.id2203.kvstore.OpResponse.Code;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.Routing;
import se.kth.id2203.sharedmemory.event.AR_Read_Request;
import se.kth.id2203.sharedmemory.event.AR_Read_Response;
import se.kth.id2203.sharedmemory.event.AR_Write_Request;
import se.kth.id2203.sharedmemory.event.AR_Write_Response;
import se.kth.id2203.sharedmemory.port.ReadImposeWriteConsult;
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
    protected final Positive<Routing> route = requires(Routing.class);
    protected final Positive<ReadImposeWriteConsult> riwc = requires(ReadImposeWriteConsult.class);
    //******* Fields ******
    final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private HashMap<Integer, KVEntry> store;
    private HashMap<UUID, NetAddress> pending;

    //******* Constructor ******
    public KVService(KVServiceInit init) {
        this.store = init.getStore();
        this.pending = new HashMap<>();
    }

    //******* Handlers ******
    protected final ClassMatchedHandler<Operation, Message> opHandler = new ClassMatchedHandler<Operation, Message>() {

        @Override
        public void handle(Operation content, Message context) {
            LOG.info("Got operation {}! Now implement me please :)", content);
            trigger(new Message(self, context.getSource(), (new OpResponse(content.id, Code.NOT_IMPLEMENTED))), net);

        }

    };

    protected final ClassMatchedHandler<GetRequest, Message> getHandler = new ClassMatchedHandler<GetRequest, Message>() {

        @Override
        public void handle(GetRequest content, Message context) {
            LOG.info("Got operation {}!", content);
            //trigger(new Message(self, context.getSource(), new GetResponse(content.id, Code.OK, value)), net);
            trigger(new AR_Read_Request(content.key.hashCode()), riwc);
            pending.put(content.id, context.getSource());
        }

    };
    protected final ClassMatchedHandler<PutRequest, Message> putHandler = new ClassMatchedHandler<PutRequest, Message>() {

        @Override
        public void handle(PutRequest content, Message context) {
            LOG.info("Got operation {}!", content);
            trigger(new AR_Write_Request(content.getValue()), riwc);
            pending.put(content.id, context.getSource());

        }

    };
    protected final Handler<AR_Read_Response> ar_read_responseHandler = new Handler<AR_Read_Response>() {
        @Override
        public void handle(AR_Read_Response ar_read_response) {
            LOG.debug("AR_Read_Response " + ar_read_response.getValue());
            UUID uid = pending.keySet().iterator().next();
            NetAddress address = pending.get(uid);
            KVEntry value = ar_read_response.getValue();
            if (value == null) {
                trigger(new Message(self, address, new GetResponse(uid, Code.NOT_FOUND, value)), net);
            } else {
                trigger(new Message(self, address, new GetResponse(uid, Code.OK, value)), net);
            }
            pending.remove(uid);

        }
    };
    protected final Handler<AR_Write_Response> ar_write_responseHandler = new Handler<AR_Write_Response>() {
        @Override
        public void handle(AR_Write_Response ar_write_response) {
            LOG.debug("AR_Write_Response ");
            UUID uid = pending.keySet().iterator().next();
            NetAddress address = pending.get(uid);
            trigger(new Message(self, address, new PutResponse(uid, Code.OK)), net);
            pending.remove(uid);
        }
    };

    {
        subscribe(opHandler, net);
        subscribe(getHandler, net);
        subscribe(putHandler, net);
        subscribe(ar_read_responseHandler, riwc);
        subscribe(ar_write_responseHandler, riwc);
    }

}
