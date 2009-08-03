package org.mule.galaxy.view;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.security.User;

public class View implements Identifiable {
    private String id;
    private User user;
    private String name;
    private String query;
    private boolean freeform;
    
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
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getQuery() {
        return query;
    }
    public void setQuery(String query) {
        this.query = query;
    }
    public boolean isFreeform() {
        return freeform;
    }
    public void setFreeform(boolean freeform) {
        this.freeform = freeform;
    }
}
