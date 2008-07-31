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

public class WWorkspace implements IsSerializable {
    private String id;
    private String name;
    private Collection<WWorkspace> workspaces;
    private String path;
    private String defaultLifecycleId;
    
    public WWorkspace() {
        super();
    }
    public WWorkspace(String id2, String name2, String path) {
        this.id = id2;
        this.name = name2;
        this.path = path;
    }
    
    public String getDefaultLifecycleId() {
        return defaultLifecycleId;
    }
    public void setDefaultLifecycleId(String defaultLifecycleId) {
        this.defaultLifecycleId = defaultLifecycleId;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Collection<WWorkspace> getWorkspaces() {
        return workspaces;
    }
    public void setWorkspaces(Collection<WWorkspace> workspaces) {
        this.workspaces = workspaces;
    }
    
}
