package se.kth.id2203.kvstore;

import se.sics.kompics.Init;

import java.util.HashMap;

/**
 * Created by 62maxime on 16/02/2017.
 */
public class KVServiceInit extends Init<KVService> {

    private HashMap<Integer, KVEntry> store;

    public KVServiceInit(HashMap<Integer, KVEntry> store) {
        this.store = store;
    }

    public HashMap<Integer, KVEntry> getStore() {
        return store;
    }
}
