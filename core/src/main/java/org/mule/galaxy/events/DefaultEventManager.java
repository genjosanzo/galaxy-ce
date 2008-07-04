package org.mule.galaxy.events;

import org.mule.galaxy.events.annotations.BindToEvent;
import org.mule.galaxy.events.annotations.BindToEvents;
import org.mule.galaxy.events.annotations.OnEvent;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class DefaultEventManager implements EventManager {

    protected final Object listenersLock = new Object();

    protected LinkedHashMap<Class, List<GalaxyEventListener>> listeners = new LinkedHashMap<Class, List<GalaxyEventListener>>();

    public DefaultEventManager(final List<?> newListeners) {
        for (Object listener : newListeners) {
            addListener(listener);
        }


    }

    public void addListener(final Object listenerCandidate) {
        if (listenerCandidate == null) {
            throw new NullPointerException("Listener can't be null");
        }

        // get event binding annotation
        final Class<?> clazz = listenerCandidate.getClass();

        final String[] eventNames;
        GalaxyEventListener adapter = null;
        if (clazz.isAnnotationPresent(BindToEvent.class)) {
            eventNames = new String[] {clazz.getAnnotation(BindToEvent.class).value()};
            Method[] methods = clazz.getDeclaredMethods();
            // TODO detect and fail on multipe OnEvent entry points
            for (final Method method : methods) {
                if (method.isAnnotationPresent(OnEvent.class)) {
                    adapter = new GalaxyEventListener() {
                        private Object delegate = listenerCandidate;

                        public void onEvent(final GalaxyEvent event) {
                            try {
                                method.invoke(delegate, event);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException itex) {
                                final Throwable cause = itex.getCause();
                                throw new RuntimeException(cause != null ? cause : itex);
                            }
                        }
                    };


                }
            }
        } else if (clazz.isAnnotationPresent(BindToEvents.class)) {
            eventNames = clazz.getAnnotation(BindToEvents.class).value();
        } else {
            throw new IllegalArgumentException(clazz.getName() + " doesn't have a BindToEvent(s) annotation");
        }
        
        for (String eventName : eventNames) {
            registerListener(adapter, eventName);
        }

        //synchronized (listenersLock) {
            // TODO reimplement
            // check for duplicate registration, this could be a programming error
            // due to a missing paired call of removeListener() earlier
            //for (Iterator<GalaxyEventListener> it = listeners.iterator(); it.hasNext();) {
            //    GalaxyEventListener reference = it.next();
            //    if (listener == reference) {
            //        System.out.println(">>> Listener " + listener + " already registered. Duplicate listenerCandidate registration could be a " +
            //                           "programming error due to a missing removeListener() call earlier. Ignoring this request.");
            //    }
            //}
            //listeners.add(listener);
        //}
    }

    // TODO refactor and optimize for multiple event bindings for a single listener probably
    protected void registerListener(final GalaxyEventListener listener, final String eventName) {
        // get event name and load its class
        String evtClassName = "org.mule.galaxy.events." + eventName + "Event";
        ClassLoader current = Thread.currentThread().getContextClassLoader();
        synchronized (listenersLock) {
            try {
                Class<GalaxyEvent> eventClass = (Class<GalaxyEvent>) Class.forName(evtClassName, true, current);
                List<GalaxyEventListener> evtListeners = listeners.get(eventClass);
                if (evtListeners == null) {
                    evtListeners = new LinkedList<GalaxyEventListener>();
                }
                evtListeners.add(listener);
                listeners.put(eventClass, evtListeners);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeListener(final GalaxyEventListener listener) {
        //synchronized (listenersLock) {
        //    for (int i = 0; i < listeners.size(); i++) {
        //        GalaxyEventListener reference = listeners.get(i);
        //        if (reference == null || reference == listener) {
        //            listeners.remove(reference);
        //        }
        //    }
        //}
    }

    public void fireEvent(final GalaxyEvent event) {
        synchronized (listenersLock) {
            List<GalaxyEventListener> eventListeners = listeners.get(event.getClass());

            if (eventListeners != null && !eventListeners.isEmpty()) {
                for (GalaxyEventListener listener : eventListeners) {
                    // TODO blocking/non-blocking
                    listener.onEvent(event);
                }
            }
        }
    }

}
