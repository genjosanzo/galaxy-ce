package org.mule.galaxy.impl.event;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.annotation.OnEvent;
import org.springframework.aop.support.AopUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springmodules.jcr.SessionFactory;

/**
 * Delegates to a listener observing multiple events (through the {@link org.mule.galaxy.event.annotation.BindToEvents} annotation and thus
 * having multiple entry points annotated with {@link org.mule.galaxy.event.annotation.OnEvent}.
 */
class DelegatingMultiEventListener extends AbstractDelegatingGalaxyEventListener {
    private final Object delegate;

    private Map<Class<? extends GalaxyEvent>, Method> eventToMethodMap = new HashMap<Class<? extends GalaxyEvent>, Method>();

    public DelegatingMultiEventListener(final Object listenerCandidate, 
                                        final ThreadPoolTaskExecutor executor,
                                        final SessionFactory sessionFactory) {
        super(listenerCandidate, executor, sessionFactory);
        delegate = listenerCandidate;
        // discover and initialize event-to-method mappings
        Class clazz;
        if (AopUtils.isAopProxy(listenerCandidate)) {
            clazz = AopUtils.getTargetClass(listenerCandidate);
        } else {
            clazz = listenerCandidate.getClass();
        }

        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(OnEvent.class)) {
                validateMethodParams(method);
                Class<? extends GalaxyEvent> paramType = method.getParameterTypes()[0].asSubclass(GalaxyEvent.class);
                eventToMethodMap.put(paramType, method);
            }
        }
    }

    public void onEvent(final GalaxyEvent event) {
        Method method = eventToMethodMap.get(event.getClass());

        if (method == null) {
            throw new IllegalArgumentException(
                    String.format("Event %s is not supported by this listener. Supported types are %s",
                                  event.getClass().getName(), eventToMethodMap.keySet())
            );
        }

        internalOnEvent(event, method);
    }

    public Object getDelegateListener() {
        return delegate;
    }
}
