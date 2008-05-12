package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WArtifactType;

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
