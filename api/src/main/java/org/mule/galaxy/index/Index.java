package org.mule.galaxy.index;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

public class Index implements Identifiable {

    private String id;
    private String description;
    private String mediaType;
    private Set<QName> documentTypes;
    private Class<?> queryType;
    private String indexer;
    private Map<String,String> configuration;

    @SuppressWarnings("unchecked")
    public Index(String description, 
                 String mediaType,
                 Class<?> queryType,
                 String indexer,
                 Map<String, String> configuration) {
        this(description, mediaType, (Set<QName>) null, queryType, indexer, configuration);
    }
    
    @SuppressWarnings("unchecked")
    public Index(String description, 
                 String mediaType,
                 QName documentType, 
                 Class<?> queryType,
                 String indexer,
                 Map<String, String> configuration) {
        this(description, mediaType, Collections.singleton(documentType), queryType, indexer, configuration);    
    }
    
    public Index(String description, 
                 String mediaType,
                 Set<QName> documentTypes, 
                 Class<?> queryType,
                 String indexer,
                 Map<String, String> configuration) {
        super();
        this.description = description;
        this.mediaType = mediaType;
        this.documentTypes = documentTypes;
        this.queryType = queryType;
        this.indexer = indexer;
        this.configuration = configuration;
    }

    public Index() {
        super();
    }
    
    public String getIndexer() {
        return indexer;
    }
    
    public void setIndexer(String indexer) {
        this.indexer = indexer;
    }

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
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

    public Map<String, String> getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
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

    public String toString()
    {
        final StringBuffer sb = new StringBuffer();
        sb.append("Index");
        sb.append(", id='").append(id).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", queryType=").append(queryType);
        sb.append(", configuration='").append(configuration).append('\'');        
        sb.append('}');
        return sb.toString();
    }
}
