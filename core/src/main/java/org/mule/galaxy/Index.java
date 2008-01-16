package org.mule.galaxy;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.impl.jcr.onm.OneToMany;

public class Index implements Identifiable {
    public enum Language  {
        GROOVY,
        XPATH,
        XQUERY
    }
    private String id;
    private String name;
    private String expression;
    private Language language;
    private Set<QName> documentTypes;
    private Class<?> queryType;
    
    public Index(String id, String name, Language language, Class<?> queryType, 
                 String expression, QName documentType) {
        super();
        this.id = id;
        this.name = name;
        this.expression = expression;
        this.language = language;
        this.documentTypes = new HashSet<QName>();
        this.documentTypes.add(documentType);
        
        this.queryType = queryType;
    }
    
    public Index(String id, String name, Language language, Class<?> queryType, 
                 String expression, Set<QName> documentTypes) {
        super();
        this.id = id;
        this.name = name;
        this.expression = expression;
        this.language = language;
        this.documentTypes = documentTypes;
        this.queryType = queryType;
    }
    
    public Index() {
        super();
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public Language getLanguage() {
        return language;
    }
    public void setLanguage(Language language) {
        this.language = language;
    }
    @OneToMany(treatAsField=true)
    public Set<QName> getDocumentTypes() {
        return documentTypes;
    }
    public void setDocumentTypes(Set<QName> documentTypes) {
        this.documentTypes = documentTypes;
    }
    public Class<?> getQueryType() {
        return queryType;
    }
    public void setQueryType(Class<?> queryType) {
        this.queryType = queryType;
    }


    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("Index");
        sb.append(", id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", language=").append(language);
        sb.append(", queryType=").append(queryType);
        sb.append(", expression='").append(expression).append('\'');        
        sb.append('}');
        return sb.toString();
    }
}
