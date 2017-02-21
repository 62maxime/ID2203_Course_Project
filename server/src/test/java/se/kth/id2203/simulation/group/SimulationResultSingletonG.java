package se.kth.id2203.simulation.group;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by ralambom on 21/02/17.
 */
public class SimulationResultSingletonG implements SimulationResultMapG {
    public static SimulationResultMapG instance = null;

    public synchronized static SimulationResultMapG getInstance() {
        ClassLoader myClassLoader = SimulationResultSingletonG.class.getClassLoader();
        if (instance == null) {
            if (!myClassLoader.toString().startsWith("sun.")) {
                try {
                    ClassLoader parentClassLoader = SimulationResultSingletonG.class.getClassLoader().getParent();
                    Class otherClassInstance = parentClassLoader.loadClass(SimulationResultSingletonG.class.getName());
                    Method getInstanceMethod = otherClassInstance.getDeclaredMethod("getInstance", new Class[]{});
                    Object otherAbsoluteSingleton = getInstanceMethod.invoke(null, new Object[]{});
                    instance = (SimulationResultMapG) Proxy.newProxyInstance(myClassLoader,
                            new Class[]{SimulationResultMapG.class},
                            new PassThroughProxyHandler(otherAbsoluteSingleton));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                instance = new SimulationResultSingletonG();
            }
        }

        return instance;
    }

    private SimulationResultSingletonG() {
    }

    private ConcurrentHashMap<String, Object> entries = new ConcurrentHashMap<>();

    @Override
    public void put(String key, Object o) {
        entries.put(key, o);
    }

    @Override
    public <T> T get(String key, Class<T> tpe) {
        return (T) entries.get(key);
    }

    @Override
    public  ConcurrentHashMap<String, Object> getEntries() {return entries;}

}