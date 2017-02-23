package se.kth.id2203.multipaxos.event;

import se.kth.id2203.kvstore.Operation;
import se.sics.kompics.KompicsEvent;

/**
 * Created by ralambom on 23/02/17.
 */
public class AscAbort implements KompicsEvent {

    private Operation operation;

    public AscAbort(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
