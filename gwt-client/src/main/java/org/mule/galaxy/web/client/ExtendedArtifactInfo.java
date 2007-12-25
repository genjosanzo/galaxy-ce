package org.mule.galaxy.web.client;

import java.util.ArrayList;
import java.util.List;

public class ExtendedArtifactInfo extends BasicArtifactInfo{
    private List properties = new ArrayList();
    private List comments = new ArrayList();
    private String description;
    
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List getProperties() {
        return properties;
    }

    public List getComments() {
        return comments;
    }
    
}
