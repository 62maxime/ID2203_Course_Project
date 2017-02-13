package se.kth.id2203.epfd.event;

import se.kth.id2203.epfd.component.EpfdInit;
import se.sics.kompics.KompicsEvent;

import java.io.Serializable;

/**
 * Created by ralambom on 13/02/17.
 */
public class Reset implements KompicsEvent, Serializable {

    private static final long serialVersionUID = -1859892943260698565L;

    private final EpfdInit init;

    public Reset(EpfdInit init) {
        this.init = init;
    }

    public EpfdInit getInit() {
        return init;
    }
}
