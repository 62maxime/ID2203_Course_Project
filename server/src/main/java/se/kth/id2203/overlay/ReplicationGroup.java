package se.kth.id2203.overlay;

import se.kth.id2203.networking.NetAddress;

import java.util.List;
import java.util.Map;
import java.util.NavigableSet;

/**
 * Created by 62maxime on 12/02/2017.
 */
public class ReplicationGroup {
    private Integer lowerBound;
    private Integer upperBound;
    private NavigableSet<NetAddress> nodes;

    public ReplicationGroup(Integer lowerBound, Integer upperBound, NavigableSet<NetAddress> netAddresses) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.nodes = netAddresses;
    }

    public boolean between(Integer key) {
        if (lowerBound == upperBound) {
            return true;
        } else if (lowerBound < upperBound) {
            return ((lowerBound < key) &&  (key <= upperBound));
        } else {
            return ((lowerBound < key) || (key <= upperBound ));
        }
    }

    public void removeNode (NetAddress address) {
        nodes.remove(address);
    }

    public void addNode (NetAddress address) {
        nodes.add(address);
    }

    public NavigableSet<NetAddress> getNodes() {
        return nodes;
    }
}
