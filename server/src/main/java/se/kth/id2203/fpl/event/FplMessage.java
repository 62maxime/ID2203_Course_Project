package se.kth.id2203.fpl.event;

import se.sics.kompics.KompicsEvent;
import se.sics.kompics.PatternExtractor;

import java.io.Serializable;

/**
 * Created by YannL on 22/02/2017.
 */
public class FplMessage implements KompicsEvent, Serializable {

    private final KompicsEvent message;
    private final Integer orderSender;

    public FplMessage(KompicsEvent message, Integer orderSender) {
        this.message = message;
        this.orderSender = orderSender;
    }

    public KompicsEvent getMessage() {
        return message;
    }

    public Integer getOrderSender() {
        return orderSender;
    }
}
