package org.mule.galaxy.impl.render;

import org.mule.galaxy.render.ItemRenderer;
import org.mule.galaxy.render.RendererManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

public class RendererManagerImpl implements RendererManager {
    private Map<QName, ItemRenderer> artifactViews = new HashMap<QName, ItemRenderer>();
    private ItemRenderer defaultView = new DefaultEntryRenderer();

    public ItemRenderer getDefaultRenderer() {
        return defaultView;
    }

    public ItemRenderer getRenderer(QName documentType) {
        ItemRenderer view = artifactViews.get(documentType);
        if (view != null) {
            return view;
        }
        
        return defaultView;
    }

    public void addRenderer(ItemRenderer view, QName... documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }

    public void addRenderer(ItemRenderer view, Collection<QName> documentTypes) {
        for (QName q : documentTypes) {
            artifactViews.put(q, view);
        }
    }
    
}
