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

/**
 * "Web user"
 */
public class WUser implements IsSerializable  {
    private String name;
    private String id;
    private String username;
    private String email;
   
    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private Collection groupIds;
    
    /**
     * @gwt.typeArgs <java.lang.String>
     */
    private Collection permissions;
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public Collection getPermissions() {
        return permissions;
    }
    public void setPermissions(Collection permissions) {
        this.permissions = permissions;
    }
    public Collection getGroupIds() {
        return groupIds;
    }
    public void setGroupIds(Collection groupIds) {
        this.groupIds = groupIds;
    }
    
}
