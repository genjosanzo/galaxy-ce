package org.mule.galaxy;

import java.util.Collection;
import java.util.Date;

public interface ActivityManager {
    String SYSTEM_USER = "system";
    
    public enum EventType {
        ERROR,
        WARNING,
        INFO
    }
    
    void logActivity(String user, String activity, EventType eventType);
    
    Collection<Activity> getActivities(Date from, Date to, String user, EventType eventType);
}
