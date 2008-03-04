package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WIndex implements IsSerializable {
    
    private String id;
    private String name;
    private String expression;
    private String indexer;
    private String resultType;
    
    /**
     * @gwt.typeArgs <java.lang.String> 
     */
    private Collection documentTypes;
    
    public WIndex(String id, String name, 
                  String expression, 
                  String indexer, String resultType,
                  Collection documentTypes) {
        super();
        this.id = id;
        this.name = name;
        this.expression = expression;
        this.indexer = indexer;
        this.resultType = resultType;
        this.documentTypes = documentTypes;
    }
    
    public WIndex() {
        super();
    }
    
    public Collection getDocumentTypes() {
        return documentTypes;
    }
    public void setDocumentTypes(Collection documentTypes) {
        this.documentTypes = documentTypes;
    }
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
    public String getExpression() {
        return expression;
    }
    public void setExpression(String expression) {
        this.expression = expression;
    }
    public String getIndexer() {
        return indexer;
    }
    public void setIndexer(String indexer) {
        this.indexer = indexer;
    }
    public String getResultType() {
        return resultType;
    }
    public void setResultType(String resultType) {
        this.resultType = resultType;
    }
    
}
