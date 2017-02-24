package se.kth.id2203.kvstore;

import se.kth.id2203.networking.NetAddress;

/**
 * Created by 62maxime on 23/02/2017.
 */
public class CasRequest extends Operation {

    private KVEntry oldValue;
    private KVEntry newValue;

    public CasRequest(String key, KVEntry oldValue, KVEntry newValue) {
        super(key);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public CasRequest(String key, NetAddress source, KVEntry oldValue, KVEntry newValue) {
        super(key, source);
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public KVEntry getOldValue() {
        return oldValue;
    }

    public KVEntry getNewValue() {
        return newValue;
    }

    @Override
    public String toString() {
        return "CasRequest{" +
                "id=" + id +
                ", key=" + key +
                ", oldValue=" + oldValue +
                ", newValue=" + newValue +
                '}';
    }
}
