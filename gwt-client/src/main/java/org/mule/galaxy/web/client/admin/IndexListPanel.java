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
import org.mule.galaxy.web.rpc.WIndex;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;

import java.util.Collection;
import java.util.Iterator;

public class IndexListPanel
    extends AbstractAdministrationComposite
{

    public IndexListPanel(AdministrationPanel a) {
        super(a);
    }
    
    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Indexes");
        
        table.setText(0, 0, "Index");
        table.setText(0, 1, "Language");
        table.setText(0, 2, "Query Type");
        
        adminPanel.getRegistryService().getIndexes(new AbstractCallback(adminPanel) {

            public void onSuccess(Object result) {
                Collection indexes = (Collection) result;
                
                int i = 1;
                for (Iterator itr = indexes.iterator(); itr.hasNext();) {
                    final WIndex idx = (WIndex) itr.next();
                    
                    String type = idx.getIndexer();
                    if ("org.mule.galaxy.impl.index.GroovyIndexer".equalsIgnoreCase(type))
                    {
                        table.setText(i, 0, idx.getDescription());
                    }
                    else 
                    {
                        Hyperlink hyperlink = new Hyperlink(idx.getDescription(), 
                                                            "indexes_" + idx.getId());
                        
                        table.setWidget(i, 0, hyperlink);
                    }
                    
                    if ("xpath".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "XPath");
                    }
                    else if ("xquery".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "XQuery");
                    }
                    else if ("groovy".equalsIgnoreCase(type))
                    {
                        table.setText(i, 1, "Groovy");
                    }
                    else
                    {
                        table.setText(i, 1, type);
                    }
                    table.setText(i, 2, idx.getResultType());
                    table.getRowFormatter().setStyleName(i, "artifactTableEntry");
                    i++;
                }
            }
            
        });
    }
}
