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
    private Collection<String> documentTypes;
    
    public WIndex(String id, String description, 
                  String mediaType,
                  String property,
                  String expression, 
                  String indexer, String resultType,
                  Collection<String> documentTypes) {
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
    
    public Collection<String> getDocumentTypes() {
        return documentTypes;
    }
    public void setDocumentTypes(Collection<String> documentTypes) {
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
