package se.kth.id2203.simulation.group;

import com.google.common.collect.TreeMultimap;
import junit.framework.Assert;
import org.junit.Test;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.LookupTable;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

import java.util.HashMap;
import java.util.Map;

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

        HashMap<NetAddress, LookupTable> map = SimulationResult.getResultTable();
        TreeMultimap<Integer, NetAddress> tmp = TreeMultimap.create();
        for(Map.Entry<NetAddress, LookupTable> entry : map.entrySet()) {
            // Check that there is 6 nodes in every node's lookup table
            Assert.assertEquals(6,entry.getValue().getNodes().size());
            // Check that each node only belongs to one replication group
            TreeMultimap<Integer, NetAddress> partitions = entry.getValue().getPartitions();
            int i = 0;
            for (int key :
                    partitions.keySet()) {
                if (partitions.get(key).contains(entry.getKey())) {
                    i++;
                }
            }
            Assert.assertEquals(1, i);
            // Check that there is 2 nodes in each node's replication group
            Assert.assertEquals(2, entry.getValue().getKey(entry.getKey()).getNodes().size());
            //Check that the partitions are the same for everyone
            if(tmp.containsValue(entry.getKey())) {
                Assert.assertEquals(tmp, partitions);
            }
            else {
                tmp = partitions;
            }
        }
    }
}
