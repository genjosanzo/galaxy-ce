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

package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class PropertyDescriptorListPanel
    extends AbstractAdministrationComposite
{
    public PropertyDescriptorListPanel(AdministrationPanel a) {
        super(a);
    }
    
    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Properties");
        
        table.setText(0, 0, "Property");
        table.setText(0, 1, "Description");
        
        adminPanel.getRegistryService().getPropertyDescriptors(false, new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection props = (Collection) result;
                
                int i = 1;
                for (Iterator itr = props.iterator(); itr.hasNext();) {
                    final WPropertyDescriptor prop = (WPropertyDescriptor) itr.next();

                    String propName = prop.getName();
                    if (propName == null || propName.trim().length() == 0) {
                        propName = "<empty>";
                    }
                    Hyperlink hyperlink = new Hyperlink(propName,
                                                        "properties/" + prop.getId());
                    
                    table.setWidget(i, 0, hyperlink);
                    table.setText(i, 1, prop.getDescription());
                    
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
