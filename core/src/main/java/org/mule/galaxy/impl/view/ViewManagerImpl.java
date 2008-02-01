package org.mule.galaxy.impl.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.ArtifactTypeView;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.ViewManager;

public class ViewManagerImpl implements ViewManager {
    private Map<QName, ArtifactTypeView> artifactViews = new HashMap<QName, ArtifactTypeView>();
    private ArtifactTypeView defaultView = new DefaultArtifactTypeView();

    public ArtifactTypeView getArtifactTypeView(String contentType) {
        return defaultView;
    }

    public ArtifactTypeView getArtifactTypeView(QName documentType) {
        ArtifactTypeView view = artifactViews.get(documentType);
        if (view != null) {
            return view;
        }
        
        return defaultView;
    }

    public void addView(ArtifactTypeView view, QName... documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }

    public void addView(ArtifactTypeView view, Collection<QName> documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }
    
}
