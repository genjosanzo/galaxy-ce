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
import org.mule.galaxy.web.rpc.WArtifactType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Iterator;

public class ArtifactTypeListPanel extends AbstractAdministrationComposite {
    
    public ArtifactTypeListPanel(AdministrationPanel a) {
        super(a);
    }

    public void onShow() {
        super.onShow();
        
        final FlexTable table = createTitledRowTable(panel, "Artifact Types");

        table.setText(0, 0, "Description");
        table.setText(0, 1, "Media Type");
        table.setText(0, 2, "Document Types");

        adminPanel.getRegistryService().getArtifactTypes(new AbstractCallback(adminPanel) {

            public void onSuccess(Object arg0) {
                showArtifactTypes(table, (Collection)arg0);
            }

        });
    }

    protected void showArtifactTypes(FlexTable table, Collection lifecycles) {
         int i = 1;
         for (Iterator itr = lifecycles.iterator(); itr.hasNext();) {
             final WArtifactType at = (WArtifactType)itr.next();

             Hyperlink atLink = new Hyperlink(at.getDescription(), "artifact-types/" + at.getId());
             
             table.setWidget(i, 0, atLink);
             table.setText(i, 1, at.getMediaType());
             if (at.getDocumentTypes() != null) {
                 FlowPanel docTypes = new FlowPanel();
                 for (Iterator dtItr = at.getDocumentTypes().iterator(); dtItr.hasNext();) {
                    String s = (String)dtItr.next();
                    docTypes.add(new Label(s));
                 }
                 table.setWidget(i, 2, docTypes);
             } else {
                 table.setText(i, 2, "");
             }
             
             table.getRowFormatter().setVerticalAlign(i, HasVerticalAlignment.ALIGN_TOP);
             i++;
         }
     }
}
