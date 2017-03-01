package se.kth.id2203.multipaxos.event;

import se.kth.id2203.kvstore.Operation;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 23/02/17.
 */
public class AscAbort implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -7900641561747447256L;
    private Operation operation;

    public AscAbort(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }
}
