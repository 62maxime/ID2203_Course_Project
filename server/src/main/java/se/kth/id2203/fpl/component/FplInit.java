package se.kth.id2203.fpl.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by YannL on 22/02/2017.
 */
public class FplInit extends Init<FIFOPerfectPointToPointLinks> {

    public final NetAddress self;
    public final Set<NetAddress> topology;
    public final Map<NetAddress, Integer> lsn;
    public final Map<NetAddress, Integer> next;

    public FplInit(NetAddress self, Set<NetAddress> topology) {
        this.self = self;
        this.topology = topology;

        this.lsn = new HashMap<>();
        this.next = new HashMap<>();

        for (NetAddress addr : topology) {
            lsn.put(addr, 0);
            next.put(addr, 1);
        }
    }
}
