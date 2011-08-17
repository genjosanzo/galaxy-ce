package org.mule.galaxy.impl.event;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mule.galaxy.event.GalaxyEvent;
import org.mule.galaxy.event.annotation.Async;
import org.mule.galaxy.impl.jcr.JcrUtil;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.SessionFactory;

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

    protected final SessionFactory sessionFactory;
    
    public AbstractDelegatingGalaxyEventListener(final Object listenerCandidate,
                                                 final ThreadPoolTaskExecutor executor,
                                                 final SessionFactory sessionFactory) {
        this.delegate = listenerCandidate;
        this.executor = executor;
        this.sessionFactory = sessionFactory;
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
        final MethodInvoker wrapper = new MethodInvoker(event, method, sessionFactory);

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
                    logger.error("Rejected async event " + event, e.getCause());
                }
            } else {
                // get hold of the native ThreadPoolExecutor which can return Futures
                ThreadPoolExecutor exec = executor.getThreadPoolExecutor();
                System.out.println("Active count: " + exec.getActiveCount() + " Task count: " + exec.getTaskCount());
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
        private final SessionFactory sessionFactory;

        public MethodInvoker(final GalaxyEvent event, final Method method, SessionFactory sessionFactory) {
            this.event = event;
            this.method = method;
            this.sessionFactory = sessionFactory;
        }
        
        public void run() {
            try {
                SecurityUtils.doPrivileged(new Runnable() {
                    public void run() {
                        runAsAdmin();
                    }
                }); 
            } catch (Throwable e) {
                //When executed by a threadpool exception thrown by run method is not re-thrown by execute method
                //Intercept exception here
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to execute <"+this.method+"> on <"+this.event+">", e);
                }
            }
        }
        
        public void runAsAdmin() {
            // allow a null sessionFactory so its easier to run tests
            if (sessionFactory != null) {
                try {
                    JcrUtil.doInTransaction(sessionFactory, new JcrCallback() {
                        public Object doInJcr(Session session) throws IOException, RepositoryException {
                            runInTransaction();
                            return null;
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException();
                } catch (RepositoryException e) {
                    throw new RuntimeException();
                }
            } else {
                runInTransaction();
            }
        }

        public void runInTransaction() {
            try {
                // special handling of dynamic proxies, otherwise
                // actual method doesn't match an instance (it's a proxy class now)
                if (Proxy.isProxyClass(delegate.getClass())) {
                    final InvocationHandler invocationHandler = Proxy.getInvocationHandler(delegate);
                    invocationHandler.invoke(delegate, method, new Object[]{event});
                } else {
                    // straight method invocation on an object, no bells and whistles
                    method.invoke(delegate, event);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException itex) {
                final Throwable cause = itex.getTargetException();
                if (cause == null) {
                    throw new RuntimeException(itex);
                }
                // Throw RuntimeException as long as it didn't fail because the session is closed.
                if (cause.getMessage() != null && !cause.getMessage().contains("this session has been closed")) {
                    throw new RuntimeException(cause);
                }
            } catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    t = ((InvocationTargetException) t).getTargetException();
                }
                throw new RuntimeException(t);
            }
        }
    }
}
