package org.mule.galaxy.artifact;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.mule.galaxy.Identifiable;
import org.mule.galaxy.mapping.OneToMany;

public class ArtifactType implements Identifiable, Serializable {
    private String id;
    private String description;
    private Set<QName> documentTypes;
    private String contentType;
    private Set<String> fileExtensions;
    
    public ArtifactType() {
    }
    
    public ArtifactType(String description, String contentType, String fileExtension, QName... documentTypes) {
        this.description = description;
        this.contentType = contentType;
        
        if (documentTypes != null) {
            for (QName d : documentTypes) {
                addDocumentType(d);
            }
        }

        if (fileExtension != null) {
            fileExtensions = new HashSet<String>();
            fileExtensions.add(fileExtension);
        }
    }

    public ArtifactType(String description, String contentType, Set<String> fileExtensions, List<QName> documentTypes) {
        this.description = description;
        this.contentType = contentType;
        this.fileExtensions = fileExtensions;

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
    @OneToMany(deref=false)
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
    
    @OneToMany(deref=true)
    public Set<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(Set<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }
    
}
