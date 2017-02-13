package se.kth.id2203.beb.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by YannL on 13/02/2017.
 */
public class BebInit extends Init<BestEffortBroadcast> {

    public final NetAddress self;
    public final Set<NetAddress> topology;

    public BebInit(NetAddress self, Set<NetAddress> topology) {
        this.self = self;
        this.topology = topology;
    }

    public BebInit(NetAddress self) {
        this(self, new HashSet<NetAddress>());
    }
}
