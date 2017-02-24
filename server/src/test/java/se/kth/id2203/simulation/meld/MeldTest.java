package se.kth.id2203.simulation.meld;

import org.junit.Test;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

/**
 * Created by YannL on 24/02/2017.
 */
public class MeldTest {
    @Test
    /**
     *  Test the meld with only correct nodes :
     *  must elect the higher ranked node
     */
    public void simpleMeldTest() {
        long seed = 123;
        final int servers = 6;

        SimulationScenario.setSeed(seed);
        SimulationScenario meldSimulationScenario = new ScenarioGen.SimpleSimu(servers);
        meldSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     *  Test the meld with one faulty node which is not the leader :
     *  must elect the higher ranked node and then nothing should happens after the node crashed
     */
    public void killSimpleNode() {
        long seed = 123;
        final int servers = 6;

        SimulationScenario.setSeed(seed);
        SimulationScenario meldSimulationScenario = new ScenarioGen.KillOneNode(servers);
        meldSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     *  Test the meld with one faulty node which is the leader :
     *  must elect a new leader once the old leader has crashed
     */
    public void killActualLeader() {
        long seed = 123;
        final int servers = 6;

        SimulationScenario.setSeed(seed);
        SimulationScenario meldSimulationScenario = new ScenarioGen.KillActualLeader(servers);
        meldSimulationScenario.simulate(LauncherComp.class);
    }

    @Test
    /**
     *  Test the meld with (x-1) faulty nodes (higher ranked) :
     *  a new leader must be elected after each crash
     */
    public void killAllExceptOne() {
        long seed = 123;
        final int servers = 6;

        SimulationScenario.setSeed(seed);
        SimulationScenario meldSimulationScenario = new ScenarioGen.KillAllExceptOne(servers);
        meldSimulationScenario.simulate(LauncherComp.class);
    }
}
