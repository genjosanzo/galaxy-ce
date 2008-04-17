package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WIndex implements IsSerializable {
    
    private String id;
    private String description;
    private String expression;
    private String indexer;
    private String resultType;
    private String property;
    private String mediaType;
    
    /**
     * @gwt.typeArgs <java.lang.String> 
     */
    private Collection documentTypes;
    
    public WIndex(String id, String description, 
                  String mediaType,
                  String property,
                  String expression, 
                  String indexer, String resultType,
                  Collection documentTypes) {
        super();
        this.id = id;
        this.description = description;
        this.property = property;
        this.mediaType = mediaType;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
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

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    
    
}
