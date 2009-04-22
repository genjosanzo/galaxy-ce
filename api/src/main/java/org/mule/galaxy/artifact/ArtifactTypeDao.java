package org.mule.galaxy.artifact;

import javax.xml.namespace.QName;

import org.mule.galaxy.Dao;

public interface ArtifactTypeDao extends Dao<ArtifactType> {
    public ArtifactType getArtifactType(String contentType, QName documentType);
    
    public ArtifactType getArtifactType(String fileExtension);
}
