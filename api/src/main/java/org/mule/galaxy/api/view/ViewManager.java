package org.mule.galaxy.api.view;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface ViewManager {
    
    ArtifactTypeView getArtifactTypeView(String contentType);
    
    ArtifactTypeView getArtifactTypeView(QName documentType);
    
    void addView(ArtifactTypeView view, QName... documentTypes);
    
    void addView(ArtifactTypeView view, Collection<QName> documentTypes);
}
