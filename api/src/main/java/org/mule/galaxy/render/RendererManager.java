package org.mule.galaxy.render;

import java.util.Collection;

import javax.xml.namespace.QName;

public interface RendererManager {
    
    ItemRenderer getDefaultRenderer();
    
    ItemRenderer getRenderer(QName documentType);
    
    void addRenderer(ItemRenderer view, QName... documentTypes);
    
    void addRenderer(ItemRenderer view, Collection<QName> documentTypes);
}
