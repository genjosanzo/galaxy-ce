/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.galaxy.api.artifact;

import org.mule.galaxy.api.Identifiable;
import org.mule.galaxy.api.jcr.onm.OneToMany;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

import javax.xml.namespace.QName;

/**
 * TODO
 */

public class ArtifactType implements Identifiable
{
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

    public ArtifactType(String description, String contentType, List<QName> documentTypes) {
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
    @OneToMany(treatAsField=true)
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