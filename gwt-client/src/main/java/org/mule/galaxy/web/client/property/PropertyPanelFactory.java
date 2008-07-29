package org.mule.galaxy.web.client.property;

import java.util.HashMap;
import java.util.Map;

import org.mule.galaxy.web.rpc.WProperty;

public class PropertyPanelFactory {
    
    public PropertyPanel createRenderer(String ext, boolean multivalued) {
        if ("lifecycleExtension".equals(ext)) {
            return new LifecyclePropertyPanel();
        } else if (multivalued) {
            return new SimpleListPropertyPanel();
        } else {
            return new SimplePropertyPanel();
        }
    }
}
