package se.kth.id2203.epfd.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

import java.util.Set;

/**
 * Created by ralambom on 11/02/17.
 */
public class EpfdInit extends Init<Epfd> {

    private final NetAddress address;
    private final Set<NetAddress> listAddresses;
    private long delta;
    private long initialPeriod;

    public EpfdInit(NetAddress address, Set<NetAddress> listAddresses, long delta, long initialPeriod) {
        this.address = address;
        this.listAddresses = listAddresses;
        this.delta = delta;
        this.initialPeriod = initialPeriod;
    }

    public NetAddress getSelfAddress() {
        return address;
    }

    public Set<NetAddress> getAllAddresses() {
        return listAddresses;
    }

    public long getDelta() {
        return delta;
    }

    public long getInitialPeriod() {
        return initialPeriod;
    }
}
