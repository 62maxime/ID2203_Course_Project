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

import se.kth.id2203.ParentComponent;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;
import se.sics.kompics.network.Address;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.adaptor.Operation1;
import se.sics.kompics.simulator.adaptor.Operation3;
import se.sics.kompics.simulator.adaptor.distributions.extra.BasicIntSequentialDistribution;
import se.sics.kompics.simulator.events.system.StartNodeEvent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Lars Kroll <lkroll@kth.se>
 */
public abstract class ScenarioGen {

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
                    if (self != 1) { // don't put this at the bootstrap server, or it will act as a bootstrap client
                        config.put("id2203.project.bootstrap-address", bsAdr);
                    }
                    return config;
                }
            };
        }
    };

    private static final Operation1 startClientOp = new Operation1<StartNodeEvent, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self) {
            return new StartNodeEvent() {
                final NetAddress selfAdr;
                final NetAddress bsAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("192.168.1." + self), 45678);
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
                    return ScenarioClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("id2203.project.address", selfAdr);
                    config.put("id2203.project.bootstrap-address", bsAdr);
                    config.put("id2203.project.client", self);
                    config.put("id2203.project.type", 1);
                    return config;
                }
            };
        }
    };

    private static final Operation3 startReadClientOp = new Operation3<StartNodeEvent, Integer, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer clientNumber, final Integer self, final Integer server) {
            return new StartNodeEvent() {
                final NetAddress selfAdr;
                final NetAddress bsAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("192.168.2." + self), 45678);
                        bsAdr = new NetAddress(InetAddress.getByName("192.168.0." + server), 45678);
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
                    return ScenarioClient.class;
                }

                @Override
                public String toString() {
                    return "StartClient<" + selfAdr.toString() + ">";
                }

                @Override
                public Init getComponentInit() {
                    return Init.NONE;
                }

                @Override
                public Map<String, Object> initConfigUpdate() {
                    HashMap<String, Object> config = new HashMap<>();
                    config.put("id2203.project.address", selfAdr);
                    config.put("id2203.project.bootstrap-address", bsAdr);
                    config.put("id2203.project.client", clientNumber);
                    config.put("id2203.project.type", 0);
                    return config;
                }
            };
        }

    };

    public static SimulationScenario oneClient(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess startClients = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };
                startCluster.start();
                startClients.startAfterTerminationOf(20000, startCluster);
                terminateAfterTerminationOf(20000, startClients);
            }
        };
    }

    public static SimulationScenario twoClientsSameOperation(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess startWrite1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startRead1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(1),
                                new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(2));
                    }
                };
                StochasticProcess startWrite2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(2));
                    }
                };
                StochasticProcess startRead2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(2),
                                new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(2));
                    }
                };
                startCluster.start();
                startWrite1.startAfterTerminationOf(20000, startCluster);
                startRead1.startAfterTerminationOf(1000, startWrite1);
                startWrite2.startAfterTerminationOf(1000, startRead1);
                startRead2.startAfterTerminationOf(1000, startWrite2);
                terminateAfterTerminationOf(1000, startRead2);
            }
        };
    }

    public static SimulationScenario twoClientsAlternate(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess startWrite1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startRead1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(2),
                                new BasicIntSequentialDistribution(1), new BasicIntSequentialDistribution(2));
                    }
                };
                StochasticProcess startRead2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(1),
                                new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(1));
                    }
                };
                startCluster.start();
                startWrite1.startAfterTerminationOf(20000, startCluster);
                startRead1.startAfterTerminationOf(1000, startWrite1);
                startRead2.startAfterTerminationOf(2000, startRead1);
                terminateAfterTerminationOf(1000, startRead2);
            }
        };
    }

    public static SimulationScenario threeClientsConcurrentOperation(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess startWrite1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startRead1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(2),
                                new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(2));
                    }
                };
                StochasticProcess startRead2 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(3),
                                new BasicIntSequentialDistribution(3), new BasicIntSequentialDistribution(3));
                    }
                };
                startCluster.start();
                startWrite1.startAfterTerminationOf(20000, startCluster);
                startRead1.startAfterTerminationOf(20000, startCluster);
                startRead2.startAfterTerminationOf(2000, startRead1);
                terminateAfterTerminationOf(1000, startRead2);
            }
        };
    }
    public static SimulationScenario failedWrite(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess startWrite1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };
                StochasticProcess startRead1 = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startReadClientOp, new BasicIntSequentialDistribution(2),
                                new BasicIntSequentialDistribution(2), new BasicIntSequentialDistribution(2));
                    }
                };
                startCluster.start();
                startWrite1.startAfterTerminationOf(20000, startCluster);
                startRead1.startAfterTerminationOf(2000, startWrite1);
                terminateAfterTerminationOf(1000, startRead1);
            }
        };
    }

    public static SimulationScenario simpleCas(final int servers) {
        return new SimulationScenario() {
            {
                StochasticProcess startCluster = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(servers, startServerOp, new BasicIntSequentialDistribution(1));
                    }
                };

                StochasticProcess casAndRead = new StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(1000));
                        raise(1, startClientOp, new BasicIntSequentialDistribution(1));
                    }
                };

                startCluster.start();
                casAndRead.startAfterTerminationOf(20000, startCluster);
                terminateAfterTerminationOf(1000, casAndRead);
            }
        };
    }


}
