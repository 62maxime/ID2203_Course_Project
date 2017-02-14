package se.kth.id2203.simulation.beb;

import junit.framework.Assert;
import org.junit.Test;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebTest {

    @Test
    public void simpleBebTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario bebSimulationScenario = se.kth.id2203.simulation.beb.ScenarioGen.bebSimulation();
        bebSimulationScenario.simulate(LauncherComp.class);
    }
}
