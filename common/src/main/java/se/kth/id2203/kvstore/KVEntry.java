package se.kth.id2203.kvstore;

import java.io.Serializable;

/**
 * Created by 62maxime on 16/02/2017.
 */
public class KVEntry implements Serializable {


    private static final long serialVersionUID = 6326281416016333606L;
    private Integer key;
    private Integer value;

    public KVEntry(Integer key, Integer value) {
        this.key = key;
        this.value = value;
    }


    public KVEntry(KVEntry kvEntry) {
        this.key = kvEntry.getKey();
        this.value = kvEntry.getValue();
    }

    public Integer getValue() {
        return value;
    }


    public Integer getKey() {
        return key;
    }

    public void setValue(Integer value) {
        this.value = value;
    }


    @Override
    public String toString() {
        return "KVEntry{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

}
