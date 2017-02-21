package se.kth.id2203.simulation.group;

/**
 * Created by ralambom on 21/02/17.
 */
import java.util.concurrent.ConcurrentHashMap;

public interface SimulationResultMapG {
    public void put(String key, Object o);
    public <T> T get(String key, Class<T> tpe);
    public ConcurrentHashMap<String, Object> getEntries();
}
