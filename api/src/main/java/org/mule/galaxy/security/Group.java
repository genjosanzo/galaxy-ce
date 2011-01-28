package org.mule.galaxy.security;

import java.io.Serializable;

import org.mule.galaxy.Identifiable;

public class Group implements Identifiable, Serializable
{
    private String id;
    private String name;
    private String description;

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
    public Group(String id, String name, String description) {
        super();
        this.id = id;
        this.name = name;
        this.description = description;
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
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
