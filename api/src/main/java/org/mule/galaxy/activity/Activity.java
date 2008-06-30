package org.mule.galaxy.activity;

import java.io.Serializable;
import java.util.Calendar;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.Item;
import org.mule.galaxy.activity.ActivityManager.EventType;
import org.mule.galaxy.security.User;

public class Activity implements Identifiable, Serializable {
    private String id;
    private User user;
    private EventType eventType;
    private Calendar date;
    private String message;
    private Item item;
    
    public Activity(User user, EventType eventType, Calendar date, String message) {
        super();
        this.user = user;
        this.eventType = eventType;
        this.date = date;
        this.message = message;
    }

    public Activity(User user, EventType eventType, Calendar date, Item item, String message) {
        super();
        this.user = user;
        this.eventType = eventType;
        this.date = date;
        this.message = message;
        this.item = item;
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
    public User getUser() {
        return user;
    }
    public void setUser(User user) {
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

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}
