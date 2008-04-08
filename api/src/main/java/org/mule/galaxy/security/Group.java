package org.mule.galaxy.security;

import org.mule.galaxy.Identifiable;

import java.io.Serializable;

public class Group implements Identifiable, Serializable
{
    private String id;
    private String name;
    
    public Group() {
        super();
    }
    public Group(String name) {
        super();
        this.name = name;
    }
    public Group(String id, String name) {
        super();
        this.id = id;
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
