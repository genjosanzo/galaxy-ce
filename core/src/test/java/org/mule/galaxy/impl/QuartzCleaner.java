package org.mule.galaxy.impl;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/**
 * Removes an left over jobs from the scheduler so we always start with a clean db after tests run.
 */
public class QuartzCleaner {
    private Scheduler scheduler;
    
    public void clean() throws SchedulerException {
        for (String group : scheduler.getJobGroupNames()) {
            for (String name : scheduler.getJobNames(group)) {
                scheduler.deleteJob(name, group);
            }
        }
    }

    public void setScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    
}
