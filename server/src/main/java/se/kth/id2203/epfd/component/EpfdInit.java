package se.kth.id2203.epfd.component;

import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.Init;

/**
 * Created by ralambom on 11/02/17.
 */
public class EpfdInit extends Init<Epfd> {

    private final NetAddress address;
    private long delta;
    private long initialPeriod;

    public EpfdInit(NetAddress address, long delta, long initialPeriod) {
        this.address = address;
        this.delta = delta;
        this.initialPeriod = initialPeriod;
    }

    public NetAddress getSelfAddress() {
        return address;
    }

    public long getDelta() {
        return delta;
    }

    public long getInitialPeriod() {
        return initialPeriod;
    }
}
