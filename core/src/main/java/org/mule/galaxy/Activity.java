package org.mule.galaxy;

import java.util.Calendar;

import org.mule.galaxy.ActivityManager.EventType;

public class Activity implements Identifiable {
    private String id;
    private String user;
    private EventType eventType;
    private Calendar date;
    private String message;
    
    public Activity(String user, EventType eventType, Calendar date, String message) {
        super();
        this.user = user;
        this.eventType = eventType;
        this.date = date;
        this.message = message;
    }
    public Activity() {
        super();
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public EventType getEventType() {
        return eventType;
    }
    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }
    public Calendar getDate() {
        return date;
    }
    public void setDate(Calendar date) {
        this.date = date;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    
    
}
