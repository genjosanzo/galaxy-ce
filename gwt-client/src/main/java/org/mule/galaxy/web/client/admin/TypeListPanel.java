/*
 * $Id$
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

package org.mule.galaxy.web.client.admin;

import java.util.Collections;
import java.util.List;

import org.mule.galaxy.web.client.util.WTypeComparator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

public class TypeListPanel
    extends AbstractAdministrationComposite
{
    protected List<WType> types;

    public TypeListPanel(AdministrationPanel a) {
        super(a);
    }
    
    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Properties");
        
        table.setText(0, 0, "Name");
        table.setText(0, 1, "Mixins");
        
        adminPanel.getRegistryService().getTypes(new AbstractCallback<List<WType>>(adminPanel) {

            public void onSuccess(List<WType> o) {
                Collections.sort(o, new WTypeComparator());
                TypeListPanel.this.types = o;
                
                int i = 1;
                for (WType type : types) {
                    String propName = type.getName();
                    if (propName == null || propName.trim().length() == 0) {
                        propName = "<empty>";
                    }
                    
                    if (type.isSystem()) {
                        table.setText(i, 0, type.getName() + " (Read Only)");
                    } else {
                        Hyperlink hyperlink = new Hyperlink(propName,
                                                            "types/" + type.getId());
                        
                        table.setWidget(i, 0, hyperlink);
                    }
                    
                    StringBuilder mixins = new StringBuilder();
                    if (type.getMixinIds() != null) {
                        boolean first = true;
                        for (String id : type.getMixinIds()) {
                            if (first) {
                                first = false;
                            } else {
                                mixins.append(", ");
                            }
                            
                            mixins.append(getType(id).getName());
                        }
                        
                    }
                    table.setText(i, 1, mixins.toString());
                    
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
    
    protected WType getType(String id) {
        for (WType t : types) {
            if (id.equals(t.getId())) return t;
        }
        return null;
    }
}
