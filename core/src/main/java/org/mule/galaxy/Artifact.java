package org.mule.galaxy;


import java.util.Calendar;
import java.util.Set;

import javax.activation.MimeType;
import javax.xml.namespace.QName;

import org.mule.galaxy.lifecycle.Phase;

/**
 * An artifact which can be versioned inside a repository. Can be anything
 * from a configuration file to a wsdl to a jar etc.
 */
public interface Artifact  {
    String getId();
    
    Workspace getWorkspace();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    void setDescription(String name);
    
    MimeType getContentType();
    
    QName getDocumentType();
    
    void setDocumentType(QName documentType);
    
    Set<ArtifactVersion> getVersions();

    ArtifactVersion getVersion(String versionName);

    ArtifactVersion getLatestVersion();
    
    public void setProperty(String name, Object value);
    
    Object getProperty(String name);
    
    public Phase getPhase();
}
