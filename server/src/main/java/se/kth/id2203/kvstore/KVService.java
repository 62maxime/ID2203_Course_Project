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

import java.util.LinkedList;
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
    private LinkedList<Message> pending;
    private boolean busy = false;

    //******* Constructor ******
    public KVService(KVServiceInit init) {
        this.pending = new LinkedList<>();
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
            if (!busy) {
                busy = true;
                LOG.info("Got operation {}!", content);
                trigger(new AR_Read_Request(content.id, content.key.hashCode()), riwc);
                pending.addFirst(context);
            } else {
                LOG.info("Got operation -> Pending {}", content.id);
                pending.addLast(context);
            }
        }

    };
    protected final ClassMatchedHandler<PutRequest, Message> putHandler = new ClassMatchedHandler<PutRequest, Message>() {

        @Override
        public void handle(PutRequest content, Message context) {
            if (!busy) {
                busy = true;
                LOG.info("Got operation {}!", content);
                trigger(new AR_Write_Request(content.id, content.key.hashCode(), content.getValue()), riwc);
                pending.addFirst(context);
            } else {
                LOG.info("Got operation -> Pending {}", content.id);
                pending.addLast(context);
            }

        }

    };
    protected final Handler<AR_Read_Response> ar_read_responseHandler = new Handler<AR_Read_Response>() {
        @Override
        public void handle(AR_Read_Response ar_read_response) {
            LOG.debug("AR_Read_Response " + ar_read_response.getValue());
            UUID uuid = ar_read_response.getUuid();
            NetAddress address = pending.removeFirst().getSource();
            KVEntry value = ar_read_response.getValue();
            if (value == null) {
                trigger(new Message(self, address, new GetResponse(uuid, Code.NOT_FOUND, value)), net);
            } else {
                trigger(new Message(self, address, new GetResponse(uuid, Code.OK, value)), net);
            }
            nextTask();
        }
    };
    protected final Handler<AR_Write_Response> ar_write_responseHandler = new Handler<AR_Write_Response>() {
        @Override
        public void handle(AR_Write_Response ar_write_response) {
            LOG.debug("AR_Write_Response ");
            UUID uuid = ar_write_response.getUuid();
            NetAddress address = pending.removeFirst().getSource();
            trigger(new Message(self, address, new PutResponse(uuid, Code.OK)), net);
            nextTask();

        }
    };

    /**
     * Ensure that only one operation is done per process
     */
    private final void nextTask() {
        busy = false;
        if (pending.isEmpty()) {
            return;
        }
        Message message = pending.removeFirst();
        Object o = message.extractPattern().cast(message.extractValue());
        if (o instanceof GetRequest) {
            LOG.info("Pending -> GetRequest");
            getHandler.handle((GetRequest) o, message);
        } else if (o instanceof PutRequest) {
            LOG.info("Pending -> PutRequest");
            putHandler.handle((PutRequest) o, message);
        } else {
            LOG.info("Pending -> None");
        }

    }


    {
        subscribe(opHandler, net);
        subscribe(getHandler, net);
        subscribe(putHandler, net);
        subscribe(ar_read_responseHandler, riwc);
        subscribe(ar_write_responseHandler, riwc);
    }

}
