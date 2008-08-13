package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemInfo implements IsSerializable {
    protected String id;
    protected List<WProperty> properties = new ArrayList<WProperty>();
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public List<WProperty> getProperties() {
        return properties;
    }
    public void setProperties(List<WProperty> properties) {
        this.properties = properties;
    }
    
    public WProperty getProperty(String name) {
        for (Iterator<WProperty> itr = properties.iterator(); itr.hasNext();) {
            WProperty p = itr.next();
            
            if (name.equals(p.getName())) {
                return p;
            }
        }
        
        return null;
    }
}
