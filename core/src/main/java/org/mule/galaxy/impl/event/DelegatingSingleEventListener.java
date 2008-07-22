package org.mule.galaxy.impl.event;

import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.event.annotation.BindToEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.core.task.TaskRejectedException;

/**
 * Delegates to a single method marked with the {@link org.mule.galaxy.event.annotation.OnEvent} annotation.
 */
class DelegatingSingleEventListener implements DelegatingGalaxyEventListener {
    protected static final int TIMEOUT_NOT_SET = -1;
    private final Object delegate;
    private final Method method;
    private DefaultEventManager defaultEventManager;

    public DelegatingSingleEventListener(final DefaultEventManager defaultEventManager, final Annotation annotation, final Object listenerCandidate, final Method method) {
        this.defaultEventManager = defaultEventManager;
        this.method = method;
        MethodParamValidator.validateMethodParam(method);
        final String eventName = ((BindToEvent) annotation).value() + "Event";
        final String callbackParam = method.getParameterTypes()[0].getSimpleName();
        if (!callbackParam.equals(eventName)) {
            throw new IllegalArgumentException(
                    String.format("Listener %s is bound to the %s, but " +
                                  "callback method param %s doesn't match it.",
                                  listenerCandidate.getClass().getName(),
                                  eventName, callbackParam));
        }
        delegate = listenerCandidate;
    }

    public void onEvent(final GalaxyEvent event) {
        final MethodInvoker wrapper = new MethodInvoker(event);
        if (method.isAnnotationPresent(Async.class)) {
            Async async = method.getAnnotation(Async.class);
            final long timeout = async.timeoutMs();

            // if timeout has not been set
            if (timeout == TIMEOUT_NOT_SET) {
                try {
                    defaultEventManager.getExecutor().execute(wrapper);
                } catch (TaskRejectedException e) {
                    defaultEventManager.logger.error("Rejected async event " + event, e.getMostSpecificCause());
                }
            } else {
                // get hold of the native ThreadPoolExecutor which can return Futures
                ThreadPoolExecutor exec = defaultEventManager.getExecutor().getThreadPoolExecutor();
                // provide the means for cancellation via a cancellable Future
                Future<?> cancellableTask = exec.submit(wrapper);

                // wait, but no longer than timeout
                try {
                    // don't care about the returned result, just timeout
                    cancellableTask.get(timeout, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    // preserve interruption status
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    defaultEventManager.logger.error("Failed to process event " + event, e.getCause());
                } catch (TimeoutException e) {
                    if (defaultEventManager.logger.isWarnEnabled()) {
                        defaultEventManager.logger.warn("Async event processing timed out, cancelling the task for " + event + ". Listener: " + delegate);
                    }
                    // interrupt if running
                    cancellableTask.cancel(true);
                }
            }
        } else {
            wrapper.run();
        }
    }

    public Object getDelegateListener() {
        return delegate;
    }

    /**
     * A helper class converting reflection failures to RuntimeExceptions.
     */
    private class MethodInvoker implements Runnable {
        private final GalaxyEvent event;

        public MethodInvoker(final GalaxyEvent event) {
            this.event = event;
        }

        public void run() {
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
}
