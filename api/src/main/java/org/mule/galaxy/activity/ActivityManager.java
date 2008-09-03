package org.mule.galaxy.activity;

import java.util.Collection;
import java.util.Date;

import org.mule.galaxy.security.AccessException;
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

    String SYSTEM = "system";
    
    Collection<Activity> getActivities(Date from, Date to, String user, 
        EventType eventType, int start, int results, boolean ascending) throws AccessException;
    
    void logActivity(String activity, EventType eventType);
    
    void logActivity(String activity, EventType eventType, User user, String itemId);
}
