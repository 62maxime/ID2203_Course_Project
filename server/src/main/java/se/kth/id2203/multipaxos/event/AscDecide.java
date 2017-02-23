package se.kth.id2203.multipaxos.event;

import se.kth.id2203.kvstore.Operation;
import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

/**
 * Created by ralambom on 23/02/17.
 */
public class AscDecide implements KompicsEvent, PatternExtractor<Class, Operation> {

    private Operation operation;

    public AscDecide(Operation operation) {
        this.operation = operation;
    }

    public Operation getOperation() {
        return operation;
    }

    @Override
    public Class extractPattern() {
        return operation.getClass();
    }

    @Override
    public Operation extractValue() {
        return operation;
    }
}
