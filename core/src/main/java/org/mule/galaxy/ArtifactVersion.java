package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;

import javax.wsdl.Definition;

import org.w3c.dom.Document;


public interface ArtifactVersion {
    
    public Calendar getCreated();
    
    /**
     * Get the version label - i.e. "1.0".
     * @return
     */
    public String getVersionLabel();
    
    /**
     * Get a Java API friendly representation of this document. This may be something
     * like a {@link Document} or a {@link Definition}.
     * @return
     */
    public Object getData();
    
    public InputStream getStream();
    
    public Artifact getParent();

    public ArtifactVersion getPrevious();
    
    public void setProperty(String name, Object value);
    
    public Object getProperty(String name);
}
