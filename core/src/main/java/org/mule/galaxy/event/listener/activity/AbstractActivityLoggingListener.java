package org.mule.galaxy.event.listener.activity;

import org.mule.galaxy.activity.ActivityManager;

public abstract class AbstractActivityLoggingListener {

    private ActivityManager activityManager;

    public ActivityManager getActivityManager() {
        return activityManager;
    }

    public void setActivityManager(final ActivityManager activityManager) {
        this.activityManager = activityManager;
    }
}
