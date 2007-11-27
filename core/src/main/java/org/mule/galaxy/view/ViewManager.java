package org.mule.galaxy.view;

import javax.xml.namespace.QName;

public interface ViewManager {
    
    ArtifactView getArtifactView(String contentType, QName documentType);
}
