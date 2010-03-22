package org.mule.galaxy.security.ldap;

import java.util.Map;

public class LdapUserMetadata {
    private String id;
    private Map<String,Object> properties;
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public Map<String, Object> getProperties() {
        return properties;
    }
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}
