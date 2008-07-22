package org.mule.galaxy.impl.event;

import org.mule.galaxy.event.GalaxyEvent;

import java.lang.reflect.Method;

/**
 * A supporting class for delegating galaxy listener implementations.
 */
public abstract class AbstractDelegatingGalaxyEventListener implements DelegatingGalaxyEventListener {

    /**
     * Validates method parameters for the entry point to be invoked.
     * @param method method to invoke on the listener, marked with {@link org.mule.galaxy.event.annotation.OnEvent}
     */
    protected void validateMethodParams(final Method method) {
        // validate the number of parameters
        Class<?>[] paramTypes = method.getParameterTypes();
        if (paramTypes.length == 0) {
            throw new IllegalArgumentException(
                    String.format("Method %s has an @OnEvent annotation, but accepts no Galaxy event class",
                                  method.toGenericString()));
        }

        if (paramTypes.length > 1) {
            throw new IllegalArgumentException(
                    String.format("Method %s has an @OnEvent annotation, but accepts multiple parameters. Only a " +
                                  "single parameter is allowed, and it must be a Galaxy event class",
                                  method.toGenericString()));

        }

        Class<?> paramType = paramTypes[0];
        if (!GalaxyEvent.class.isAssignableFrom(paramType)) {
            throw new IllegalArgumentException(
                    String.format("Method %s has an @OnEvent annotation, but doesn't accept a Galaxy event class",
                                  method.toGenericString()));
        }
        
    }
}
