package org.mule.galaxy.web.rpc;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WArtifactType implements IsSerializable {
    private String id;
    private String mediaType;
    private String description;
    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private Collection documentTypes;
    
    public WArtifactType() {
        super();
    }
    public WArtifactType(String id, String mediaType, String description, Collection documentTypes) {
        super();
        this.id = id;
        this.mediaType = mediaType;
        this.description = description;
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
    public String getMediaType() {
        return mediaType;
    }
    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
    public Collection getDocumentTypes() {
        return documentTypes;
    }
    public void setDocumentTypes(Collection documentTypes) {
        this.documentTypes = documentTypes;
    }
    
    
}
