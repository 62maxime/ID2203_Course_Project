package se.kth.id2203.simulation.beb;

import org.junit.Test;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebTest {

    @Test
    /**
     * Test 1 : Only correct recipients and one correct sender
     *
     * Test that all nodes deliver the message sent by the BebBroadcast
     */
    public void simpleBebTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario bebSimulationScenario = se.kth.id2203.simulation.beb.ScenarioGen.bebSimulation(1);
        bebSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     * Test 2 : One faulty recipient (other correct)  and one correct sender
     *
     * Test that all correct nodes deliver the message sent by the BebBroadcast
     */
    public void BebRecptCrashTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario bebSimulationScenario = se.kth.id2203.simulation.beb.ScenarioGen.bebSimulation(2);
        bebSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     * Test 3 : Only correct recipients and one faulty sender
     *
     * Everything can happen
     */
    public void BebSenderCrashTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario bebSimulationScenario = se.kth.id2203.simulation.beb.ScenarioGen.bebSimulation(3);
        bebSimulationScenario.simulate(LauncherComp.class);
    }
}
