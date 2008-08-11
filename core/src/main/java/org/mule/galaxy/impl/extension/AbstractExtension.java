package org.mule.galaxy.impl.extension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.extension.Extension;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.type.PropertyDescriptor;

public abstract class AbstractExtension implements Extension {
    protected String id;
    protected String name;
    protected boolean isMultivalueSupported = true;
    protected Map<String, String> queryProperties;

    public Map<String, String> getQueryProperties(PropertyDescriptor pd) {
        Map<String, String> p = new HashMap<String, String>();
        for (Map.Entry<String, String> e : queryProperties.entrySet()) {
            String property = pd.getProperty();
            if (!e.getKey().equals("")) {
                property = property + "." + e.getKey();
            }
            
            String name = e.getValue();
            if ("".equals(name)) {
                name = pd.getDescription();
            } else {
                name = pd.getDescription() + " - " + e.getValue();
            }
            
            p.put(property, name);
        }
        return p;
    }

    public void validate(Item entry, PropertyDescriptor pd, Object valueToStore) throws PolicyException {
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setQueryProperties(Map<String, String> queryProperties) {
        this.queryProperties = queryProperties;
    }

    public List<String> getPropertyDescriptorConfigurationKeys() {
        return new ArrayList<String>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMultivalueSupported() {
        return isMultivalueSupported;
    }

    public void setMultivalueSupported(boolean isMultivalueSupported) {
        this.isMultivalueSupported = isMultivalueSupported;
    }
}
