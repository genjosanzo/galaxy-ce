package org.mule.galaxy.impl.event;

import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.annotation.Async;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * A supporting class for delegating galaxy listener implementations.
 */
public abstract class AbstractDelegatingGalaxyEventListener implements DelegatingGalaxyEventListener {

    protected static final int TIMEOUT_NOT_SET = -1;

    /**
     * Logger for this class.
     */
    protected final Log logger = LogFactory.getLog(getClass());

    /**
     * Delegate listener implementation.
     */
    protected final Object delegate;

    /**
     * Thread pool for processing async events.
     */
    protected final ThreadPoolTaskExecutor executor;

    public AbstractDelegatingGalaxyEventListener(final Object listenerCandidate, final ThreadPoolTaskExecutor executor) {
        this.delegate = listenerCandidate;
        this.executor = executor;
    }

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

    /**
     * Encapsulates sync/async execution logic and exception handling.
     * @param event event to process
     * @param method method to invoke
     */
    protected void internalOnEvent(final GalaxyEvent event, final Method method) {
        final MethodInvoker wrapper = new MethodInvoker(event, method);

        if (!method.isAnnotationPresent(Async.class)) {
            // synchronous execution in the event-dispatching thread
            wrapper.run();
        } else {
            // asynchronous execution
            Async async = method.getAnnotation(Async.class);
            final long timeout = async.timeoutMs();

            // if timeout has not been set
            if (timeout == TIMEOUT_NOT_SET) {
                try {
                    // execute without timeout
                    executor.execute(wrapper);
                } catch (TaskRejectedException e) {
                    logger.error("Rejected async event " + event, e.getMostSpecificCause());
                }
            } else {
                // get hold of the native ThreadPoolExecutor which can return Futures
                ThreadPoolExecutor exec = executor.getThreadPoolExecutor();
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
                    logger.error("Failed to process event " + event, e.getCause());
                } catch (TimeoutException e) {
                    if (logger.isWarnEnabled()) {
                        logger.warn("Async event processing timed out, cancelling the task for " + event + ". Listener: " + delegate);
                    }
                    // interrupt if running
                    cancellableTask.cancel(true);
                }
            }
        }
    }

    /**
     * A helper class converting reflection failures to RuntimeExceptions.
     */
    protected class MethodInvoker implements Runnable {
        private final GalaxyEvent event;
        private Method method;

        public MethodInvoker(final GalaxyEvent event, final Method method) {
            this.event = event;
            this.method = method;
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
