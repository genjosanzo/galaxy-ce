package org.mule.galaxy.impl.render;

import org.mule.galaxy.render.ArtifactRenderer;
import org.mule.galaxy.render.RendererManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class RendererManagerImpl implements RendererManager {
    private Map<QName, ArtifactRenderer> artifactViews = new HashMap<QName, ArtifactRenderer>();
    private ArtifactRenderer defaultView = new DefaultArtifactRenderer();

    public ArtifactRenderer getArtifactRenderer(String contentType) {
        return defaultView;
    }

    public ArtifactRenderer getArtifactRenderer(QName documentType) {
        ArtifactRenderer view = artifactViews.get(documentType);
        if (view != null) {
            return view;
        }
        
        return defaultView;
    }

    public void addRenderer(ArtifactRenderer view, QName... documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }

    public void addRenderer(ArtifactRenderer view, Collection<QName> documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }
    
}
