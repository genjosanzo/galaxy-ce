package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.io.Serializable;

public class WActivity implements IsSerializable {
    private String id;
    private String username;
    private String eventType;
    private String date;
    private String message;
    private String name;
    
    public WActivity(String username, String name,
                     String eventType, String date, String message) {
        super();
        this.username = username;
        this.name = name;
        this.eventType = eventType;
        this.date = date;
        this.message = message;
    }
    public WActivity() {
        super();
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
}
