package se.kth.id2203.sharedmemory.component;

import com.google.common.base.Optional;
import se.kth.id2203.kvstore.KVEntry;
import se.kth.id2203.networking.NetAddress;
import se.sics.kompics.ComponentDefinition;

import java.util.HashMap;

/**
 * Created by 62maxime on 17/02/2017.
 */
public class RIWC extends ComponentDefinition {

    private class Triplet {
        private int ts;
        private int wr;
        private Optional<KVEntry> value;

        public Triplet(int ts, int wr, Optional<KVEntry> value) {
            this.ts = ts;
            this.wr = wr;
            this.value = value;
        }

        public int getTs() {
            return ts;
        }

        public int getWr() {
            return wr;
        }

        public Optional<KVEntry> getValue() {
            return value;
        }
    }

    // ****** State and initialization
    private NetAddress self;
    private int selfRank;
    private int ts = 0;
    private int wr = 0;
    private Optional<KVEntry> value = null;
    private int acks = 0;
    private Optional<KVEntry> readVal = null;
    private Optional<KVEntry> writeVal = null;
    private HashMap<NetAddress, Triplet> readList;
    private boolean reading = false;

    public RIWC(RIWCInit init) {
        this.self = init.getSelf();
        this.selfRank = init.getSelfRank();
        readList = new HashMap<>();
    }



}
