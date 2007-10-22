package org.mule.galaxy.impl;

import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.xml.namespace.QName;

import org.mule.galaxy.Index;
import org.mule.galaxy.util.JcrUtil;

public class IndexImpl implements Index {

    public static final String EXPRESSION = "expression";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String QUERY_TYPE = "queryType";
    public static final String DOCUMENT_TYPE = "documentType";
    public static final String LANGUAGE = "language";
    public static final String DOCUMENT_TYPE_VALUE = "value";
    
    private String id;
    private String name;
    private String expression;
    private Language language;
    private Set<QName> documentTypes;
    private Class<?> queryType;
    
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
    
}
