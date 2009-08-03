package org.mule.galaxy.impl.render;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.artifact.Artifact;
import org.mule.galaxy.render.ItemRenderer;
import org.mule.galaxy.render.RendererManager;
import org.mule.galaxy.type.Type;
import org.mule.galaxy.type.TypeManager;

public class RendererManagerImpl implements RendererManager {
    private Map<QName, ItemRenderer> docViews = new HashMap<QName, ItemRenderer>();
    private Map<String, ItemRenderer> typeViews = new HashMap<String, ItemRenderer>();
    private ItemRenderer defaultView = new DefaultEntryRenderer();

    public ItemRenderer getDefaultRenderer() {
        return defaultView;
    }

    public ItemRenderer getRenderer(Item item) {
        ItemRenderer view = null;
        
        if (item.getType().inheritsFrom(TypeManager.ARTIFACT_VERSION)) {
            Artifact a = item.getProperty("artifact");
            QName docType = a.getDocumentType();
            if (docType != null) {
                view = docViews.get(docType);
            }
        }
        
        if (view == null) {
            view = typeViews.get(item.getType().getId());
        }
        
        if (view == null) {
            view = defaultView;
        }
        
        return view;
    }

    public void addRenderer(ItemRenderer view, Type... types) {
        for (Type t : types) {
            typeViews.put(t.getId(), view);
        }
    }

    public void addRenderer(ItemRenderer view, QName... documentTypes) {
        for (QName q : documentTypes) {
            docViews.put(q, view);
        }
    }

    public void addRenderer(ItemRenderer view, Collection<QName> documentTypes) {
        for (QName q : documentTypes) {
            docViews.put(q, view);
        }
    }
    
}
