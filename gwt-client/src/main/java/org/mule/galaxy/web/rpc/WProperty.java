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
import java.util.Collection;
import java.util.List;

public class WProperty implements IsSerializable {
    private boolean locked;
    private String name;
    private String description;
    private String value;
    /*
     * @gwt.typeArgs <java.lang.String>
     */
    private List listValue;
    private String extension;
    private boolean multiValued;
    
    public WProperty(String name, String description, 
                     Object value, String extension,
                     boolean locked) {
        super();
        this.extension = extension;
        this.locked = locked;
        this.name = name;
        this.description = description;
        
        setValue(value);
    }
    
    public WProperty() {
        super();
    }
    
    public String getExtension() {
        return extension;
    }

    public boolean isMultiValued() {
        return multiValued;
    }
    
    public boolean isLocked() {
        return locked;
    }
    public void setLocked(boolean locked) {
        this.locked = locked;
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
    public String getValue() {
        return value;
    }

    public void setValue(Object value) {
        if (value instanceof String) {
            this.value = (String) value;
        } else if (value instanceof List) {
            this.listValue = (List) value;
            multiValued = true;
        } else if (value instanceof Collection) {
            this.listValue = new ArrayList();
            listValue.addAll((Collection) value);
            multiValued = true;
        }
    }

    public List getListValue() {
        return listValue;
    }

    public void setMultiValued(boolean multiValued) {
        this.multiValued = multiValued;
    }
    
}
