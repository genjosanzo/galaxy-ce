package org.mule.galaxy;

import java.util.Set;

import javax.xml.namespace.QName;

public interface Index {
    public enum Language  {
        GROOVY,
        XQUERY
    }
    
    public String getId();
    
    public void setId(String id);
    
    public String getName();
    
    public void setName(String name);
    
    public Language getLanguage();
    
    public void setLanguage(Language language);
    
    public String getExpression();
    
    public void setExpression(String expression);
    
    public Set<QName> getDocumentTypes();
    
    public void setDocumentTypes(Set<QName> types);
    
    public Class<?> getQueryType();
    
    public void setQueryType(Class<?> queryType);
}
