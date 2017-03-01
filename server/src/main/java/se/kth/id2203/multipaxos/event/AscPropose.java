package se.kth.id2203.multipaxos.event;

import se.kth.id2203.kvstore.Operation;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class AscPropose implements KompicsEvent, Serializable {

    private static final long serialVersionUID = 6001679326847903345L;
    private Operation operation;

    public AscPropose(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
