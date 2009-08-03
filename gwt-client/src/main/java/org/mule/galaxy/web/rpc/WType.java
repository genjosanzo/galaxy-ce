package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mule.galaxy.web.client.util.PropertyDescriptorComparator;

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

    /**
     * Does this type inherit from the specified candidiate?
     * @param candidate
     * @param types
     * @return
     */
    public boolean inherits(WType candidate, Map<String,WType> types) {
        if (candidate.getId().equals(getId())) {
            return true;
        }
        
        List<String> checked = new ArrayList<String>();

        return inherits(candidate, this, checked, types);
    }

    private boolean inherits(WType candidate, WType type, List<String> checked, Map<String,WType> types) {
        if (checked.contains(type.getId())) {
            return false;
        }
        checked.add(type.getId());
        
        List<String> mixinIds = type.getMixinIds();
        if (mixinIds != null) {
            for (String mixin : mixinIds) {
                if (mixin.equals(candidate.getId())) {
                    return true;
                }
                
                if (inherits(candidate, types.get(mixin), checked, types)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<WPropertyDescriptor> getAllProperties(Map<String, WType> types) {
        Set<WPropertyDescriptor> props = new HashSet<WPropertyDescriptor>();
        
        List<String> checked = new ArrayList<String>();

        collectProperties(this, props, checked, types);
        
        ArrayList list = new ArrayList(props);
        Collections.sort(list, new PropertyDescriptorComparator());
        return list;
    }


    private void collectProperties(WType type, Set<WPropertyDescriptor> props, List<String> checked, Map<String,WType> types) {
        if (checked.contains(type.getId())) {
            return;
        }
        checked.add(type.getId());
        
        if (type.getProperties() != null) {
            props.addAll(type.getProperties());
        }
        
        List<String> mixinIds = type.getMixinIds();
        if (mixinIds != null) {
            for (String mixin : mixinIds) {
                WType child = types.get(mixin);
                
                collectProperties(child, props, checked, types);
            }
        }
    }

}
