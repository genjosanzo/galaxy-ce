package org.mule.galaxy.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Removes an left over jobs from the scheduler so we always start with a clean db after tests run.
 */
public class QuartzCleaner implements ApplicationContextAware {

    private final Log logger = LogFactory.getLog(getClass());
    private ApplicationContext context;

    public void clean() throws SchedulerException {
        try {
            Scheduler scheduler = (Scheduler) context.getBean("scheduler");
            for (String group : scheduler.getJobGroupNames()) {
                for (String name : scheduler.getJobNames(group)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Deleting job: %s in group: %s", name, group));
                    }
    
                    scheduler.deleteJob(name, group);
                }
            }
        } catch (NoSuchBeanDefinitionException e) {
            
        }
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }
    
}
