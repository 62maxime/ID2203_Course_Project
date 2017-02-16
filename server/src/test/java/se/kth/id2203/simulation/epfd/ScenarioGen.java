package se.kth.id2203.simulation.epfd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation2;
import se.sics.kompics.simulator.adaptor.distributions.ConstantDistribution;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.KillNodeEvent;
import se.sics.kompics.simulator.events.system.StartNodeEvent;
import se.sics.kompics.simulator.stochastic.events.StochasticProcessEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by YannL on 16/02/2017.
 */
public abstract class ScenarioGen {
    final static Logger LOG = LoggerFactory.getLogger(ScenarioGen.class);
    final private static int nbNodesToMonitor = 5;

    final static Set<NetAddress> topologyToMonitor = new HashSet<NetAddress>() {{
        for (int i = 1; i <= nbNodesToMonitor; i++) {
            try {
                this.add(new NetAddress(InetAddress.getByName("localhost"), 10000 + i));
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }};

    static Operation1 startDetector = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("localhost"), 10000 + self);
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
                    return DetectorParent.class;
                }

                @Override
                public Init getComponentInit() {
                    return new DetectorParent.Init(selfAdr, topologyToMonitor);
                }

                @Override
                public String toString() {
                    return "StartDetector<" + selfAdr.toString() + ">";
                }
            };
        }
    };

    static Operation1 startMonitored = new Operation1<StartNodeEvent, Integer>() {
        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("localhost"), 10000 + self);
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
                    return Monitored.class;
                }

                @Override
                public Init getComponentInit() {
                    return new Monitored.Init(selfAdr);
                }

                @Override
                public String toString() {
                    return "StartMonitored<" + selfAdr.toString() + ">";
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
                        selfAdr = new NetAddress(InetAddress.getByName("localhost"), 10000 + self);
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


    public static class Simu1 extends SimulationScenario {
        public  class Detector extends StochasticProcess {
            public Detector(int self) {
                this.eventInterArrivalTime(constant(500));
                this.raise(1, startDetector, new ConstantDistribution<>(Integer.class, self));
            }
        }

        public  class Monitored extends StochasticProcess {
            public Monitored() {
                this.eventInterArrivalTime(constant(100));
                this.raise(nbNodesToMonitor, startMonitored, new BasicIntSequentialDistribution(1));
            }
        }

        public  class KillMonitored extends StochasticProcess {
            public KillMonitored() {
                this.eventInterArrivalTime(constant(4000));
                this.raise(4, killNode, new BasicIntSequentialDistribution(1));
            }
        }

        public Simu1() {
            int detector_addr = 0;
            Detector detector = new Detector(detector_addr);
            Monitored monitored = new Monitored();
            KillMonitored killMonitored = new KillMonitored();

            LOG.info("[EPFD] : TEST 1");
            monitored.start();
            detector.startAfterTerminationOf(10000, monitored);
            killMonitored.startAfterTerminationOf(10000, detector);
            terminateAfterTerminationOf(100000, detector);
        }
    }



    public static SimulationScenario Simu2 () {
        SimulationScenario scen = new SimulationScenario() {
            {
                SimulationScenario.StochasticProcess detector = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(500));
                        raise(1, startDetector, new ConstantDistribution<>(Integer.class, 0));
                    }
                };

                SimulationScenario.StochasticProcess monitored = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(nbNodesToMonitor, startMonitored, new BasicIntSequentialDistribution(1));
                    }
                };

                SimulationScenario.StochasticProcess killMonitored = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(10000));
                        raise(4, killNode, new BasicIntSequentialDistribution(1));
                    }
                };


                SimulationScenario.StochasticProcess startDied = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(10));
                        raise(1, startMonitored, new ConstantDistribution<>(Integer.class, 1));
                    }
                };

                LOG.info("[EPFD] : TEST 2");
                monitored.start();
                detector.startAfterTerminationOf(10000, monitored);
                killMonitored.startAfterTerminationOf(5000, detector);
                startDied.startAfterTerminationOf(1000, killMonitored);
                terminateAfterTerminationOf(100000, detector);
            }
        };
        return scen;
    }
}
