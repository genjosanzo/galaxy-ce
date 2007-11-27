package org.mule.galaxy.impl.view;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.ArtifactView;
import org.mule.galaxy.view.Column;
import org.mule.galaxy.view.ColumnEvaluator;
import org.mule.galaxy.view.ViewManager;

public class ViewManagerImpl implements ViewManager {
    private Map<QName, ArtifactView> artifactViews = new HashMap<QName, ArtifactView>();
    private ArtifactView defaultView = new DefaultArtifactView();

    public ArtifactView getArtifactView(String contentType) {
        return defaultView;
    }

    public ArtifactView getArtifactView(QName documentType) {
        ArtifactView view = artifactViews.get(documentType);
        if (view != null) {
            return view;
        }
        
        return defaultView;
    }

    public void addView(ArtifactView view, QName... documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }
}
