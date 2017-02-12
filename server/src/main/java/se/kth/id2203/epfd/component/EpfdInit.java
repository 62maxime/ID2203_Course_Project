package se.kth.id2203.epfd.component;

import se.sics.kompics.Init;
import se.sics.kompics.network.Address;

import java.util.Set;

/**
 * Created by ralambom on 11/02/17.
 */
public class EpfdInit extends Init<Epfd> {

    private final Address address;
    private final Set<Address> listAddresses;
    private long delta;
    private long initialPeriod;

    public EpfdInit(Address address, Set<Address> listAddresses, long delta, long initialPeriod) {
        this.address = address;
        this.listAddresses = listAddresses;
        this.delta = delta;
        this.initialPeriod = initialPeriod;
    }

    public Address getSelfAddress() {
        return address;
    }

    public Set<Address> getAllAddresses() {
        return listAddresses;
    }

    public long getDelta() {
        return delta;
    }

    public long getInitialPeriod() {
        return initialPeriod;
    }
}
