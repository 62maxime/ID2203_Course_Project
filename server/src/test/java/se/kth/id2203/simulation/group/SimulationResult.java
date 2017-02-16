package se.kth.id2203.simulation.group;

import se.kth.id2203.networking.NetAddress;
import se.kth.id2203.overlay.LookupTable;
import se.kth.id2203.overlay.ReplicationGroup;

import java.util.HashMap;

/**
 * Created by ralambom on 16/02/17.
 */
public class SimulationResult {

    private static HashMap<NetAddress, LookupTable> resultTable = new HashMap<>();

    public static void setResultTable(NetAddress address, LookupTable table) {
        resultTable.put(address, table);
    }

    public static HashMap<NetAddress, LookupTable> getResultTable() {
        return resultTable;
    }
}
