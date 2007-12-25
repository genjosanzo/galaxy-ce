package org.mule.galaxy.web.client;

import java.util.ArrayList;
import java.util.List;

public class ExtendedArtifactInfo extends BasicArtifactInfo{
    private List properties = new ArrayList();
    private List comments = new ArrayList();
    
    public List getProperties() {
        return properties;
    }

    public List getComments() {
        return comments;
    }
    
}
