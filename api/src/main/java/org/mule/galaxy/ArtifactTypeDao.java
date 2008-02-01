package org.mule.galaxy;

import javax.xml.namespace.QName;

public interface ArtifactTypeDao extends Dao<ArtifactType> {
    public ArtifactType getArtifactType(String contentType, QName documentType);
}
