package org.mule.galaxy;


import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import javax.xml.namespace.QName;

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
    
    String getContentType();
    
    void setContentType(String ct);
    
    QName getDocumentType();
    
    void setDocumentType(QName documentType);
    
    Set<ArtifactVersion> getVersions();

    ArtifactVersion getVersion(String versionName);

    ArtifactVersion getLatestVersion();
}
