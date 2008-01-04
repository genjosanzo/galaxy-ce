package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Set;

import javax.wsdl.Definition;

import org.mule.galaxy.security.User;

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
    
    void setProperty(String name, Object value) throws PropertyException;
    
    Object getProperty(String name);

    Iterator<PropertyInfo> getProperties();
    
    PropertyInfo getPropertyInfo(String name);

    void setLocked(String name, boolean locked);

    void setVisible(String property, boolean visible);
    
    /**
     * The author of this version. They may or may not be the actual author, but they
     * are the entity responsible for adding it to the repository.
     * @return
     */
    User getAuthor();
    
    Set<Dependency> getDependencies();
    
    boolean isActive();
}
