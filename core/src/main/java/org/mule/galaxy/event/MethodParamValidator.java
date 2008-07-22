package org.mule.galaxy.event;

import java.lang.reflect.Method;

/*
    Ugly static method, but otherwise we face a listener hierarchy explosion.
    TODO this class is to be refactored and will go.
 */
class MethodParamValidator {

    protected static void validateMethodParam(final Method method) {
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
