package org.mule.galaxy.view;

import javax.xml.namespace.QName;

public interface ViewManager {
    
    ArtifactView getArtifactView(String contentType);
    
    ArtifactView getArtifactView(QName documentType);
    
    void addView(ArtifactView view, QName... documentTypes);
}
