package se.kth.id2203.kvstore;

import java.io.Serializable;

/**
 * Created by 62maxime on 16/02/2017.
 */
public class KVEntry implements Serializable {

    private Integer key;
    private Integer value;

    public KVEntry(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "KVEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }
}
