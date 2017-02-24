package se.kth.id2203.kvstore;

import se.sics.kompics.Init;

import java.util.Map;

/**
 * Created by 62maxime on 16/02/2017.
 */
public class KVServiceInit extends Init<KVService> {

    private Map<Integer, KVEntry> store;

    public KVServiceInit(Map<Integer, KVEntry> store) {
        this.store = store;
    }

    public Map<Integer, KVEntry> getStore() {
        return store;
    }
}
