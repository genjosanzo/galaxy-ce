package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;

import javax.wsdl.Definition;

import org.w3c.dom.Document;


public interface ArtifactVersion {
    
    Calendar getCreated();
    
    /**
     * Get the version label - i.e. "1.0".
     * @return
     */
    String getVersionLabel();
    
    /**
     * Get a Java API friendly representation of this document. This may be something
     * like a {@link Document} or a {@link Definition}.
     * @return
     */
    Object getData();
    
    InputStream getStream();
    
    Artifact getParent();

    ArtifactVersion getPrevious();
    
    void setProperty(String name, Object value);
    
    Object getProperty(String name);
}
