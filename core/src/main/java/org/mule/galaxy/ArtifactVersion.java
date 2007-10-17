package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;


public interface ArtifactVersion {
    
    public Calendar getCreated();
    
    public String getVersion();
    
    /**
     * Get a Java friendly representation of this document.
     * @return
     */
    public Object getData();
    
    public InputStream getStream();
    
    public Artifact getParent();

    public ArtifactVersion getPrevious();
    
    public void setProperty(String name, Object value);
}
