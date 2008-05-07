package org.mule.galaxy.render;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface RendererManager {
    
    ArtifactRenderer getArtifactRenderer(String contentType);
    
    ArtifactRenderer getArtifactRenderer(QName documentType);
    
    void addRenderer(ArtifactRenderer view, QName... documentTypes);
    
    void addRenderer(ArtifactRenderer view, Collection<QName> documentTypes);
}
