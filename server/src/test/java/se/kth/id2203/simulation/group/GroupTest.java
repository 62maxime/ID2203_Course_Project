package se.kth.id2203.simulation.group;

import com.google.common.collect.TreeMultimap;
import junit.framework.Assert;
import org.junit.Test;
import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.LookupTable;
import se.sics.kompics.simulator.SimulationScenario;
import se.sics.kompics.simulator.run.LauncherComp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.Assert.fail;

/**
 * Created by ralambom on 16/02/17.
 */
public class GroupTest {

    private final SimulationResultMapG res = SimulationResultSingletonG.getInstance();
    private final ConcurrentHashMap<String, Object> map = SimulationResultSingletonG.getInstance().getEntries();


    @Test
    public void groupTest() {
        long seed = 123;
        SimulationScenario.setSeed(seed);
        SimulationScenario simpleGroupScenario = ScenarioGen.simpleOps(6);
        simpleGroupScenario.simulate(LauncherComp.class);


        HashMap<Integer, ArrayList<String>> tmp = new HashMap<Integer, ArrayList<String>>();
        for(Map.Entry<String, Object> entry : map.entrySet()) {
            HashMap<Integer, ArrayList<String>> tree = res.get(entry.getKey(), HashMap.class);
            Integer intKey = -1;

            int i = 0;
            int n = 0;
            for (int key : tree.keySet()) {
                n += tree.get(key).size();
                if (tree.get(key).contains(entry.getKey())) {
                    intKey = key;
                    i++;
                }
            }

            // Check that there is 6 nodes in every node's lookup table
            Assert.assertEquals(6, n);

            // Check that each node only belongs to one replication group
            Assert.assertEquals(1, i);

            // Check that there is 3 nodes in each node's replication group
            if(intKey != -1) {
                Assert.assertEquals(3, tree.get(intKey).size());
            }

            //Check that the partitions are the same for everyone
            if(tmp.containsKey(intKey)) {
                Assert.assertEquals(tmp, tree);
            }
            else {
                tmp = tree;
            }
        }
    }
}
