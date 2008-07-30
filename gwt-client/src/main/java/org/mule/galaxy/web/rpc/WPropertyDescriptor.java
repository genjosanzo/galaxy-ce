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

import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class WPropertyDescriptor implements IsSerializable {
    private String id;
    private boolean multiValued;
    private String name;
    private String description;
    private String extension;
    private Map configuration;
    
    public WPropertyDescriptor(String id, String name, String description, String extension, boolean multiValued, Map configuration) {
        super();
        this.id = id;
        this.extension = extension;
        this.multiValued = multiValued;
        this.name = name;
        this.description = description;
        this.configuration = configuration;
    }
    public WPropertyDescriptor() {
        super();
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public boolean isMultiValued() {
        return multiValued;
    }
    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getExtension() {
        return extension;
    }
    public void setExtension(String extension) {
        this.extension = extension;
    }
    public Map getConfiguration() {
        return configuration;
    }
    public void setConfiguration(Map configuration) {
        this.configuration = configuration;
    }
}
