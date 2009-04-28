package org.mule.galaxy.web.rpc;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WType  implements IsSerializable {
    private String id;
    private String name;
    private List<WPropertyDescriptor> properties;
    private List<String> mixinIds = new ArrayList<String>();
    private List<String> allowedChildrenIds = new ArrayList<String>();
    
    private boolean system;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WPropertyDescriptor> getProperties() {
        return properties;
    }

    public void setProperties(List<WPropertyDescriptor> properties) {
        this.properties = properties;
    }

    public List<String> getMixinIds() {
        return mixinIds;
    }

    public void setMixinIds(List<String> mixinIds) {
        this.mixinIds = mixinIds;
    }

    public List<String> getAllowedChildrenIds() {
        return allowedChildrenIds;
    }

    public void setAllowedChildrenIds(List<String> allowedChildrenIds) {
        this.allowedChildrenIds = allowedChildrenIds;
    }

    public boolean isSystem() {
        return system;
    }

    public void setSystem(boolean system) {
        this.system = system;
    }

    public void addProperty(WPropertyDescriptor id2) {
        if (properties == null) {
            properties = new ArrayList<WPropertyDescriptor>();
        }
        properties.add(id2);
    }

}
