package org.mule.galaxy.config.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;

/**
 * An application lifecycle listener to start/shutdown quartz.
 */
public class QuartzLifecycleBean implements ApplicationListener {

    private final Log logger = LogFactory.getLog(getClass());

    private Scheduler scheduler;

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public void onApplicationEvent(ApplicationEvent event) {
        try {
            if (event instanceof ContextStoppedEvent &&
                    (scheduler.isStarted() || scheduler.isInStandbyMode())) {
                scheduler.shutdown();
                if (logger.isInfoEnabled()) {
                    logger.info("Successful shutdown of the scheduler");
                }
            }
        } catch (SchedulerException ex) {
            logger.warn("Failed to shutdown scheduler", ex);
        }

        try {
            if ((event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent)
                    && !scheduler.isStarted()) {
                scheduler.start();
                if (logger.isInfoEnabled()) {
                    logger.info("Scheduler successfully started");
                }
            }
        } catch (SchedulerException ex) {
            throw new IllegalStateException("Failed to start the scheduler", ex);
        }
    }
}
