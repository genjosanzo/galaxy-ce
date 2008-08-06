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

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class EntryVersionInfo implements IsSerializable {
    private String id;
    private String versionLabel;
    private String link;
    private Date created;
    private String authorName;
    private String authorUsername;
    private boolean _default;
    private boolean enabled;
    private boolean indexInformationStale;
    
    /*
     * @gwt typeArgs org.mule.galaxy.rpc.WProperty
     */
    private List<WProperty> properties = new ArrayList<WProperty>();
    
    public EntryVersionInfo(String id, String versionLabel, 
                            Date created, boolean _default,
                               boolean enabled,
                               String authorName, 
                               String authorUsername,
                               boolean indexInformationStale) {
        super();
        this.id = id;
        this.versionLabel = versionLabel;
        this._default = _default;
        this.enabled = enabled;
        this.created = created;
        this.authorName = authorName;
        this.authorUsername = authorUsername;
        this.indexInformationStale = indexInformationStale;
    }

    public EntryVersionInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

    public boolean isIndexInformationStale() {
        return indexInformationStale;
    }

    public void setIndexInformationStale(boolean indexInformationStale) {
        this.indexInformationStale = indexInformationStale;
    }

    /**
     * @gwt typeArgs org.mule.galaxy.web.rpc.WProperty
     */
    public List<WProperty> getProperties() {
        return properties;
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isDefault() {
        return _default;
    }

    public void setDefault(boolean _default) {
        this._default = _default;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public void setAuthorUsername(String authorUsername) {
        this.authorUsername = authorUsername;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getVersionLabel() {
        return versionLabel;
    }
    
    public void setVersionLabel(String versionLabel) {
        this.versionLabel = versionLabel;
    }
    
    public String getLink() {
        return link;
    }
    
    public void setLink(String link) {
        this.link = link;
    }
    
    public Date getCreated() {
        return created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
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
    
}
