package se.kth.id2203.epfd.component;

import se.sics.kompics.timer.ScheduleTimeout;
import se.sics.kompics.timer.Timeout;

/**
 * Created by ralambom on 11/02/17.
 */
public class CheckTimeout extends Timeout {

    protected CheckTimeout(ScheduleTimeout request) {
        super(request);
    }
}
