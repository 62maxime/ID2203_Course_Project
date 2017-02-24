package se.kth.id2203.simulation.meld;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.ParentComponent;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by YannL on 24/02/2017.
 */

public abstract class ScenarioGen {
    final static Logger LOG = LoggerFactory.getLogger(se.kth.id2203.simulation.epfd.ScenarioGen.class);

    private static final Operation1 startServerOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                final NetAddress selfAdr;
                final NetAddress bsAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("192.168.0." + self), 45678);
                        bsAdr = new NetAddress(InetAddress.getByName("192.168.0.1"), 45678);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return selfAdr;
                }

                @Override
                public Class getComponentDefinition() {
                    return ParentComponent.class;
                }

                @Override
                public String toString() {
                    return "StartNode<" + selfAdr.toString() + ">";
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("id2203.project.address", selfAdr);
                    config.put("id2203.project.bootThreshold", 6);
                    config.put("id2203.project.partitionGroupNumber", 1);
                    config.put("id2203.project.replicationDelta", 6);
                    if (self != 1) { // don't put this at the bootstrap server, or it will act as a bootstrap client
                        config.put("id2203.project.bootstrap-address", bsAdr);
                    }
                    return config;
                }
            };
        }
    };

    static Operation1 killNode = new Operation1<KillNodeEvent, Integer>() {

        @Override
        public KillNodeEvent generate(final Integer self) {
            return new KillNodeEvent() {
                NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("192.168.0." + self), 45678);
                    } catch (UnknownHostException ex) {
                        throw new RuntimeException(ex);
                    }
                }

                @Override
                public Address getNodeAddress() {
                    return this.selfAdr;
                }

                @Override
                public String toString() {
                    return "KillNode<" + selfAdr.toString() + ">";
                }
            };
        }
    };


    public static abstract class MeldSimu extends SimulationScenario {
        public class StartCluster extends StochasticProcess {
            public StartCluster(final int servers) {
                eventInterArrivalTime(constant(100));
                raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
            }
        }

        public class KillNode extends StochasticProcess {
            public KillNode(final int self) {
                this.raise(1, killNode, new ConstantDistribution(Integer.class, self));
            }
        }
    }

    public static class SimpleSimu extends MeldSimu {

        public SimpleSimu(final int servers) {
            StartCluster startCluster = new StartCluster(servers);

            LOG.info("[MELD] : TEST 1");
            startCluster.start();
            terminateAfterTerminationOf(10000, startCluster);
        }
    }


    public static class KillOneNode extends MeldSimu {
        public KillOneNode(final int servers) {
            MeldSimu.StartCluster startCluster = new MeldSimu.StartCluster(servers);

            LOG.info("[MELD] : TEST 2");
            startCluster.start();

            KillNode killNode = new KillNode(servers - 1);
            killNode.startAfterTerminationOf(13000, startCluster);

            terminateAfterTerminationOf(100000, killNode);
        }
    }

    public static class KillActualLeader extends MeldSimu {
        public KillActualLeader(final int servers) {
            MeldSimu.StartCluster startCluster = new MeldSimu.StartCluster(servers);

            LOG.info("[MELD] : TEST 3");
            startCluster.start();

            KillNode killServer = new KillNode(servers);
            killServer.startAfterTerminationOf(13000, startCluster);

            terminateAfterTerminationOf(100000, killServer);
        }
    }


    public static class KillAllExceptOne extends MeldSimu {
        public KillAllExceptOne(final int servers) {
            MeldSimu.StartCluster startCluster = new MeldSimu.StartCluster(servers);

            LOG.info("[MELD] : TEST 4");
            startCluster.start();

            KillNode kill6 = new KillNode(6);
            kill6.startAfterTerminationOf(13000, startCluster);

            KillNode kill5 = new KillNode(5);
            kill5.startAfterTerminationOf(13000, kill6);

            KillNode kill4 = new KillNode(4);
            kill4.startAfterTerminationOf(13000, kill5);

            KillNode kill3 = new KillNode(3);
            kill3.startAfterTerminationOf(13000, kill4);

            KillNode kill2 = new KillNode(2);
            kill2.startAfterTerminationOf(13000, kill3);

            terminateAfterTerminationOf(100000, kill2);
        }
    }
}