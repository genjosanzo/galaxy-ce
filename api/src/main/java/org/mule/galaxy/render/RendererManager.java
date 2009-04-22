package org.mule.galaxy.render;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.mule.galaxy.Item;
import org.mule.galaxy.type.Type;

public interface RendererManager {
    
    ItemRenderer getDefaultRenderer();
    
    ItemRenderer getRenderer(Item item);

    void addRenderer(ItemRenderer view, Type... documentTypes);
    
    void addRenderer(ItemRenderer view, QName... documentTypes);
    
    void addRenderer(ItemRenderer view, Collection<QName> documentTypes);
}