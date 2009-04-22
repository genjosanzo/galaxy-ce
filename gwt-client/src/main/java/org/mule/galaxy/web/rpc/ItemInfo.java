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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ItemInfo implements IsSerializable {
    private String id;
    private List<WProperty> properties = new ArrayList<WProperty>();
    private boolean local;
    private boolean modifiable;
    private boolean deletable;
    private String name;
    private String path;
    private String parentPath;
    private String authorName;
    private String authorUsername;
    private List<WComment> comments = new ArrayList<WComment>();
    private String commentsFeedLink;
    private String artifactLink;
    private String artifactFeedLink;
    private String type;
    private String defaultLifecycleId;
    private Collection<ItemInfo> items;
    private Map<Integer, String> col2Value = new HashMap<Integer, String>();

    public void setColumn(int col, String value) {
        col2Value.put(new Integer(col), value);
    }
    
    public String getValue(int col) {
        return col2Value.get(new Integer(col));
    }
    
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    
    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public boolean isLocal() {
        return local;
    }
    public void setLocal(boolean local) {
        this.local = local;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public boolean isModifiable() {
        return modifiable;
    }
    
    public void setModifiable(boolean modifiable) {
        this.modifiable = modifiable;
    }
    
    public boolean isDeletable() {
        return deletable;
    }
    
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }
    
    public List<WProperty> getProperties() {
        return properties;
    }
    
    public void setProperties(List<WProperty> properties) {
        this.properties = properties;
    }
    
    public WProperty getProperty(String name) {
        for (Iterator<WProperty> itr = properties.iterator(); itr.hasNext();) {
            WProperty p = itr.next();
            
            if (name.equals(p.getName())) {
                return p;
            }
        }
        
        return null;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public List<WComment> getComments() {
        return comments;
    }

    public void setComments(List<WComment> comments) {
        this.comments = comments;
    }

    public String getCommentsFeedLink() {
        return commentsFeedLink;
    }

    public void setCommentsFeedLink(String commentsFeedLink) {
        this.commentsFeedLink = commentsFeedLink;
    }

    public String getArtifactLink() {
        return artifactLink;
    }

    public void setArtifactLink(String artifactLink) {
        this.artifactLink = artifactLink;
    }

    public String getArtifactFeedLink() {
        return artifactFeedLink;
    }

    public void setArtifactFeedLink(String artifactFeedLink) {
        this.artifactFeedLink = artifactFeedLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setDefaultLifecycleId(String defaultLifecycleId) {
        this.defaultLifecycleId = defaultLifecycleId;
    }

    public String getDefaultLifecycleId() {
        return defaultLifecycleId;
    }

    public Collection<ItemInfo> getItems() {
        return items;
    }

    public void setItems(Collection<ItemInfo> items) {
        this.items = items;
    }
    
}

