package org.mule.galaxy;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

public class ArtifactType implements Identifiable {
    private String id;
    private String description;
    private Set<QName> documentTypes;
    private String contentType;
    
    public ArtifactType() {
    }
    
    public ArtifactType(String description, String contentType, QName... documentTypes) {
        this.description = description;
        this.contentType = contentType;
        
        if (documentTypes != null) {
            for (QName d : documentTypes) {
                addDocumentType(d);
            }
        }
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
    public Set<QName> getDocumentTypes() {
        return documentTypes;
    }
    public void setDocumentTypes(Set<QName> documentTypes) {
        this.documentTypes = documentTypes;
    }
    public String getContentType() {
        return contentType;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public void addDocumentType(QName q) {
        if (documentTypes == null) {
            documentTypes = new HashSet<QName>();
        }
        
        documentTypes.add(q);
    }
    
}
