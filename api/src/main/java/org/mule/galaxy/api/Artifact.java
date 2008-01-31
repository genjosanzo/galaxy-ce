package org.mule.galaxy.api;


import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.mule.galaxy.api.lifecycle.Phase;

/**
 * An artifact which can be versioned inside a repository. Can be anything
 * from a configuration file to a wsdl to a jar etc.
 */
public interface Artifact  {
    String getId();
    
    String getPath();
    
    Workspace getWorkspace();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String description);
    
    MimeType getContentType();
    
    QName getDocumentType();
    
    void setDocumentType(QName documentType);
    
    List<ArtifactVersion> getVersions();

    ArtifactVersion getVersion(String versionName);

    /**
     * Get the active version of this artifact. If this hasn't been specifically set,
     * its the latest version of the artifact.
     * @return
     */
    ArtifactVersion getActiveVersion();
    
    public void setProperty(String name, Object value) throws PropertyException;
    
    Object getProperty(String name);

    boolean hasProperty(String name);
    
    public Phase getPhase();

    public void setPhase(Phase phase);

    Iterator<PropertyInfo> getProperties();
    
    PropertyInfo getPropertyInfo(String name);

    void setLocked(String name, boolean locked);

    void setVisible(String property, boolean visible);

    ContentHandler getContentHandler();

    void setContentHandler(ContentHandler contentHandler);

    Registry getRegistry();
}
