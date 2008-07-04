package org.mule.galaxy.events;

import org.mule.galaxy.events.annotations.BindToEvent;
import org.mule.galaxy.events.annotations.BindToEvents;
import org.mule.galaxy.events.annotations.OnEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
            throw new IllegalArgumentException("Listener can't be null");
        }

        // get event binding annotation
        final Class<?> clazz = listenerCandidate.getClass();

        final String[] eventNames;
        GalaxyEventListener adapter = null;
        final Annotation annotation = findAnnotation(clazz, BindToEvent.class);
        if (annotation != null) {
            eventNames = new String[] {((BindToEvent) annotation).value()};
            Method[] methods = clazz.getMethods();
            // TODO detect and fail on multipe OnEvent entry points
            for (final Method method : methods) {
                if (method.isAnnotationPresent(OnEvent.class)) {
                    adapter = new DelegatingMultiEventListener(listenerCandidate, method);
                }
            }

            // no OnEvent annotation found, fail
            if (adapter == null) {
                throw new IllegalArgumentException(String.format("Listener %s is missing an @OnEvent entry point",
                                                                 listenerCandidate.getClass().getName()));
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

    /**
     * Check for annotation presence <strong>with inheritance</strong>. I.e. if a subclass doesn't have an annotation,
     * but extends a class having one, this method would report the subclass as having the annotation.
     * @return annotation or null if none found
     */
    protected Annotation findAnnotation(Class<?> clazz, final Class<? extends Annotation> annotation) {
        boolean annotationPresent = clazz.isAnnotationPresent(annotation);
        // TODO doesn't yet handle cases when a listener implements an interface which has an annotation
        while (!annotationPresent && clazz.getSuperclass() != null) {
            clazz = clazz.getSuperclass();
            annotationPresent = clazz.isAnnotationPresent(annotation);
        }

        return annotationPresent ? clazz.getAnnotation(annotation) : null;
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

    public void removeListener(final Object listener) {
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

    /**
     * Delegates to a single method marked with the {@link OnEvent} annotation.
     */
    protected static class DelegatingGalaxyEventListener implements GalaxyEventListener {
        private final Object delegate;
        private final Method method;

        public DelegatingGalaxyEventListener(final Object listenerCandidate, final Method method) {
            this.method = method;
            delegate = listenerCandidate;
        }

        public void onEvent(final GalaxyEvent event) {
            try {
                method.invoke(delegate, event);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException itex) {
                final Throwable cause = itex.getTargetException();
                throw new RuntimeException(cause);
            }
        }
    }

    /**
     * Delegates to a listener observing multiple events (through the {@link BindToEvents} annotation and thus
     * having multiple entry points annotated with {@link OnEvent}.
     * TODO to be implemented
     */
    protected static class DelegatingMultiEventListener implements GalaxyEventListener {
        private final Object delegate;
        private final Method method;

        public DelegatingMultiEventListener(final Object listenerCandidate, final Method method) {
            this.method = method;
            delegate = listenerCandidate;
        }

        public void onEvent(final GalaxyEvent event) {
            try {
                method.invoke(delegate, event);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException itex) {
                final Throwable cause = itex.getTargetException();
                throw new RuntimeException(cause);
            }
        }
    }

    protected static interface GalaxyEventListener extends EventListener {
        void onEvent(GalaxyEvent event);
    }
}
