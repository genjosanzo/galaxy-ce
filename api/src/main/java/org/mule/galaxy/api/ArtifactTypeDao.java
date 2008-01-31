package org.mule.galaxy.api;

import org.mule.galaxy.api.artifact.ArtifactType;

import javax.xml.namespace.QName;

public interface ArtifactTypeDao extends Dao<ArtifactType> {
    public ArtifactType getArtifactType(String contentType, QName documentType);
}
