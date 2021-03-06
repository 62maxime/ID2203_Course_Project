package se.kth.id2203.simulation.beb;

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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class ScenarioGen {

    final static Logger LOG = LoggerFactory.getLogger(Sender.class);


    static Operation2 startSender = new Operation2<StartNodeEvent, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer nbNodes) {
            return new StartNodeEvent() {
                NetAddress selfAdr;

                {
                    try {
                        selfAdr = new NetAddress(InetAddress.getByName("localhost"), 10000);
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
                    return SenderParent.class;
                }

                @Override
                public Init getComponentInit() {
                    Set<NetAddress> recipients = new HashSet<>();
                    for (int i = 0; i < nbNodes; i++) {
                        try {
                            recipients.add(new NetAddress(InetAddress.getByName("localhost"), 10000 + i));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                    return new SenderParent.Init(selfAdr, recipients);
                }

                @Override
                public String toString() {
                    return "StartSender<" + selfAdr.toString() + ">";
                }
            };
        }
    };

    static Operation2 startRecipients = new Operation2<StartNodeEvent, Integer, Integer>() {

        @Override
        public StartNodeEvent generate(final Integer self, final Integer nbNodes) {

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
                    return RecipientParent.class;
                }

                @Override
                public Init getComponentInit() {
                    Set<NetAddress> recipients = new HashSet<>();
                    for (int i = 0; i < nbNodes; i++) {
                        try {
                            recipients.add(new NetAddress(InetAddress.getByName("localhost"), 10000 + i));
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                    }
                    return new RecipientParent.Init(selfAdr, recipients);
                }

                @Override
                public String toString() {
                    return "StartRecipient<" + selfAdr.toString() + ">";
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
            };
        }
    };

    public static SimulationScenario bebSimulation(final int test) {
        SimulationScenario scen = new SimulationScenario() {
            {
                SimulationScenario.StochasticProcess sender = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(500));
                        raise(1, startSender, new BasicIntSequentialDistribution(0),
                                new ConstantDistribution(Integer.class, 6));
                    }
                };

                SimulationScenario.StochasticProcess recipients = new SimulationScenario.StochasticProcess() {
                    {
                        eventInterArrivalTime(constant(100));
                        raise(5, startRecipients, new BasicIntSequentialDistribution(1),
                                new ConstantDistribution(Integer.class, 6));
                    }
                };


                if (test == 1) {
                    LOG.info("[BEB_BROADCAST] : TEST 1");

                    recipients.start();
                    sender.startAfterTerminationOf(100, recipients);
                    //terminateAfterTerminationOf(10000, recipients);
                } else if (test == 2) {
                    LOG.info("[BEB_BROADCAST] : TEST 2");
                    SimulationScenario.StochasticProcess killRecipient = new SimulationScenario.StochasticProcess() {
                        {
                            eventInterArrivalTime(constant(100));
                            raise(1, killNode, new ConstantDistribution(Integer.class, 2));
                        }
                    };

                    recipients.start();
                    sender.startAfterTerminationOf(3000, recipients);
                    killRecipient.startAfterTerminationOf(200, recipients);

                } else {
                    LOG.info("[BEB_BROADCAST] : TEST 3");
                    SimulationScenario.StochasticProcess killSender = new SimulationScenario.StochasticProcess() {
                        {
                            eventInterArrivalTime(constant(10));
                            raise(1, killNode, new ConstantDistribution(Integer.class, 0));
                        }
                    };

                    recipients.start();
                    sender.startAfterTerminationOf(100, recipients);
                    killSender.startAfterStartOf(5, sender);
                }
            }
        };

        return scen;
    }
}
