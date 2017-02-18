package se.kth.id2203.simulation.group;

import org.junit.Test;
import se.kth.id2203.overlay.LookupTable;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * Created by ralambom on 16/02/17.
 */
public class GroupTest {

    @Test
    public void groupTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleGroupScenario = ScenarioGen.simpleOps(6);
        simpleGroupScenario.simulate(LauncherComp.class);

        /***TODO:
         + check that each replication group count 2 nodes
         * check that a node only belong to one replication group
         * check that the lookup table is the same for everyone
         **/
    }
}
