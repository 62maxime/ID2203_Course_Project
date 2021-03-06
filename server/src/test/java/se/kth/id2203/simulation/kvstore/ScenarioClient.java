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
package se.kth.id2203.simulation.kvstore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.kvstore.*;
import se.kth.id2203.networking.Message;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.RouteMsg;
import se.sics.kompics.*;
import se.sics.kompics.network.Network;
import se.sics.kompics.timer.Timer;

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

/**
 * @author Lars Kroll <lkroll@kth.se>
 */
public class ScenarioClient extends ComponentDefinition {

    final static Logger LOG = LoggerFactory.getLogger(ScenarioClient.class);
    //******* Ports ******
    protected final Positive<Network> net = requires(Network.class);
    protected final Positive<Timer> timer = requires(Timer.class);
    //******* Fields ******
    private final NetAddress self = config().getValue("id2203.project.address", NetAddress.class);
    private final NetAddress server = config().getValue("id2203.project.bootstrap-address", NetAddress.class);
    private final SimulationResultMap res = SimulationResultSingleton.getInstance();
    private final Map<UUID, String> pending = new TreeMap<>();
    //******* Handlers ******
    protected final Handler<Start> startHandler = new Handler<Start>() {

        @Override
        public void handle(Start event) {
            int testNum = res.get("testNum", Integer.class);
            switch (testNum){
                case 1:
                    test1();
                    break;
                case  2:
                    test2();
                    break;
                case 3:
                    test3();
                    break;
                case 4:
                    test4();
                    break;
                case 5:
                    test5();
                    break;
                case 6:
                    test6();
                    break;
                case 7:
                    test7();
                    break;
                case 8:
                    test8();
                    break;
                case 9:
                    test9();
                    break;
                case 10:
                    test10();
                    break;
                case 11:
                    test11();
                    break;
                default:
            }

        }
    };

    Handler<Kill> killHandler = new Handler<Kill>() {
        @Override
        public void handle(Kill kill) {
            LOG.debug("Killed");

        }
    };
    protected final ClassMatchedHandler<OpResponse, Message> responseHandler = new ClassMatchedHandler<OpResponse, Message>() {

        @Override
        public void handle(OpResponse content, Message context) {
            LOG.debug("Got OpResponse: {}", content);
            String key = pending.remove(content.id);
            if (key != null) {
                res.put(key, content.status.toString());
            } else {
                LOG.warn("ID {} was not pending! Ignoring response.", content.id);
            }
        }
    };

    protected final ClassMatchedHandler<GetResponse, Message> getResponseHandler = new ClassMatchedHandler<GetResponse, Message>() {

        @Override
        public void handle(GetResponse content, Message context) {
            LOG.debug("Got GetResponse: {}", content);
            String key = pending.remove(content.id);
            int testNum = res.get("testNum", Integer.class);
            if (key != null) {
                if (content.getValue() == null) {
                    if (testNum == 4 || testNum == 5 || testNum == 11) {
                        int client = config().getValue("id2203.project.client", Integer.class);
                        res.put("client" + client, "NOT_FOUND");
                    } else {
                        res.put(key, "NOT_FOUND");
                    }
                } else {
                    if (testNum == 2 || testNum == 4  || testNum == 5 || testNum >= 8) {
                        int client = config().getValue("id2203.project.client", Integer.class);
                        res.put("client" + client, content.getValue().getValue());
                    } else if (testNum == 3) {
                        int client = config().getValue("id2203.project.client", Integer.class);
                        res.put("client" + client, content.getValue().getValue());
                        if (client == 2) {
                            sendPut("write", client);
                            res.put("client" + 1, "PUT");
                        }
                    } else {
                        res.put(key, content.getValue().getValue());
                    }

                }

            } else {
                LOG.warn("ID {} was not pending! Ignoring response.", content.id);
            }
        }
    };
    protected final ClassMatchedHandler<PutResponse, Message> putResponseHandler = new ClassMatchedHandler<PutResponse, Message>() {

        @Override
        public void handle(PutResponse content, Message context) {
            LOG.debug("Got PutResponse: {}", content);
            int testNum = res.get("testNum", Integer.class);
            String key = pending.remove(content.id);
            if (key != null) {
                res.put(key, content.status.toString());
            } else {
                LOG.warn("ID {} was not pending! Ignoring response.", content.id);
            }
            if (testNum == 6) {
                sendCas("entry6", 0, 1);
            } else if (testNum == 7) {
                sendCas("entry7", 1, 1);

            }
        }
    };

    protected final ClassMatchedHandler<CasResponse, Message> casResponseHandler = new ClassMatchedHandler<CasResponse, Message>() {

        @Override
        public void handle(CasResponse content, Message message) {
            LOG.debug("Got CasResponse: {}", content);
            int testNum = res.get("testNum", Integer.class);
            String key = pending.remove(content.id);
            if (key != null) {
                res.put(key, content.status.toString());
            } else {
                LOG.warn("ID {} was not pending! Ignoring response.", content.id);
            }
            if (testNum == 6) {
                sendGet("entry6");
            } else if (testNum == 7){
                sendGet("entry7");
            }
        }
    };

    private void sendGet(String key) {
        GetRequest op = new GetRequest(key);
        op.setSource(self);
        RouteMsg rm = new RouteMsg(op.key, op); // don't know which partition is responsible, so ask the bootstrap server to forward it
        trigger(new Message(self, server, rm), net);
        pending.put(op.id, op.key);
        LOG.info("Sending {}", op);
        res.put(op.key, "SENT");
    }

    private void sendPut(String key, Integer value) {
        PutRequest put = new PutRequest(key, new KVEntry(key.hashCode(), value));
        put.setSource(self);
        RouteMsg rm = new RouteMsg(put.key, put); // don't know which partition is responsible, so ask the bootstrap server to forward it
        trigger(new Message(self, server, rm), net);
        pending.put(put.id, put.key);
        res.put(put.key, "PUT");
    }

    private void sendCas(String key, Integer previousValue, Integer newValue){
        CasRequest cas = new CasRequest(key, new KVEntry(key.hashCode(),previousValue), new KVEntry(key.hashCode(), newValue));
        cas.setSource(self);
        RouteMsg rm = new RouteMsg(cas.key, cas);
        trigger(new Message(self, server, rm), net);
        pending.put(cas.id, cas.key);
        res.put(cas.key, "CAS");
    }

    private void test1() {
        int messages = res.get("messages", Integer.class);
        for (int i = 0; i < messages; i++) {
            sendGet("test" + i);
        }
    }

    private void test2() {
        int type = config().getValue("id2203.project.type", Integer.class);
        int client = config().getValue("id2203.project.client", Integer.class);
        if (type == 0) {
            sendGet("write");
            res.put("client" + client, "GET");
        } else {
            sendPut("write", client);
            res.put("client" + 2, "PUT");
        }
    }

    private void test3() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 2) {
            sendGet("write");
            res.put("client" + 2, "GET");
        } else if (client == 1) {
            int type = config().getValue("id2203.project.type", Integer.class);
            if (type == 1) {
                sendPut("write", client);
                res.put("client" + 2, "PUT");
            } else {
                sendGet("write");
                res.put("client" + 1, "GET");
            }

        }
    }

    private void test4() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 1) {
            sendPut("write", 2);
            res.put("client" + 2, "PUT");
            res.put("client" + 3, "PUT");
        } else {
            sendGet("write");
            res.put("client" + client, "GET");
        }
    }

    private void test5() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 1) {
            sendPut("write", 2);
            res.put("client" + 2, "PUT");
            suicide();
        } else {
            sendGet("write");
            res.put("client" + client, "GET");
        }
    }

    private void test6() {
        sendPut("entry6", 0);
    }

    private void test7() {
        sendPut("entry7", 0);
    }

    private void test8() {
        int type = config().getValue("id2203.project.type", Integer.class);
        int client = config().getValue("id2203.project.client", Integer.class);
        if (type == 1) {
            sendPut("test0", 10);
        } else {
            if (client == 1) {
                sendCas("test0", 10, 20);
            } else {
                sendGet("test0");
            }
        }
    }

    private void test9() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 1) {
            sendCas("test1", 40, 10);
        } else if (client == 2) {
            sendCas("test1", 40, 20);
        } else {
            sendGet("test1");
        }
    }

    private void test10() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 1) {
            sendCas("test2", 39, 2);
            res.put("client" + 2, "PUT");
            suicide();
        } else {
            sendGet("test2");
            res.put("client" + client, "GET");
        }
    }

    private void test11() {
        int client = config().getValue("id2203.project.client", Integer.class);
        if (client == 1) {
            sendCas("entry11", 100, 2);
            res.put("client" + 2, "PUT");
            suicide();
        } else {
            sendGet("entry11");
            res.put("client" + client, "GET");
        }
    }


    {
        subscribe(startHandler, control);
        subscribe(responseHandler, net);
        subscribe(getResponseHandler, net);
        subscribe(putResponseHandler, net);
        subscribe(casResponseHandler, net);
        subscribe(killHandler, control);
    }
}
