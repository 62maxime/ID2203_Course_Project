package se.kth.id2203.simulation.epfd;

import org.junit.Test;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * Created by YannL on 16/02/2017.
 */
public class EpfdTest {
    @Test
    /**
     * Test 1 : Kill all monitored node except one
     *
     * Test that (x-1)/x nodes are suspected by the epfd
     */
    public void simpleEpfdTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario epfdSimulationScenario = new ScenarioGen.Simu1();
        epfdSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     * Test 2 : Kill all monitored node except one and then restart one killed
     *  to test that this node is restored by the epfd
     *
     */
    public void resucitedEpfdTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario epfdSimulationScenario = ScenarioGen.Simu2();
        epfdSimulationScenario.simulate(LauncherComp.class);
    }
}
