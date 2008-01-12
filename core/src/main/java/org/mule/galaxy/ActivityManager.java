package org.mule.galaxy;

import java.util.Collection;
import java.util.Date;

import org.mule.galaxy.security.User;

public interface ActivityManager {
    
    public enum EventType {
        
        ERROR("Error"),
        WARNING("Warning"),
        INFO("Info");

        private String text;
        
        EventType(String text) {
            this.text = text;
        }

        public String getText() {
            return text;
        }
        
    }
    
    /**
     * Log a system activity.
     * @param activity
     * @param eventType
     */
    void logActivity(String activity, EventType eventType);
    
    /**
     * Log an activity from a user.
     * @param user
     * @param activity
     * @param eventType
     */
    void logActivity(User user, String activity, EventType eventType);
    
    Collection<Activity> getActivities(Date from, Date to, String user, EventType eventType, int start, int results, boolean ascending);
}
