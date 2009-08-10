/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.rpc;

import com.extjs.gxt.ui.client.data.BeanModelTag;
import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Collection;

public class WArtifactType implements IsSerializable, BeanModelTag {
    private String id;
    private String mediaType;
    private String description;
    private Collection<String> documentTypes;
    private Collection<String> fileExtensions;


    public WArtifactType() {
        super();
    }

    public WArtifactType(String id, String mediaType,
                         String description, Collection<String> documentTypes,
                         Collection<String> fileExtensions) {
        super();
        this.id = id;
        this.mediaType = mediaType;
        this.description = description;
        this.documentTypes = documentTypes;
        this.fileExtensions = fileExtensions;
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

    public Collection<String> getDocumentTypes() {
        return documentTypes;
    }

    public void setDocumentTypes(Collection<String> documentTypes) {
        this.documentTypes = documentTypes;
    }

    public Collection<String> getFileExtensions() {
        return fileExtensions;
    }

    public void setFileExtensions(Collection<String> fileExtensions) {
        this.fileExtensions = fileExtensions;
    }


}
