package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.MenuPanelPageInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WArtifactType;

public class ArtifactTypeListPanel extends AbstractComposite {
    private AdministrationPanel adminPanel;
    private FlowPanel panel;

    public ArtifactTypeListPanel(AdministrationPanel a) {
        super();

        this.adminPanel = a;

        panel = new FlowPanel();

        initWidget(panel);
    }

    public void onShow() {
        panel.clear();

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

             Hyperlink atLink = new Hyperlink(at.getDescription(), "artifact-type-" + at.getId());
             MenuPanelPageInfo page = new MenuPanelPageInfo(atLink, adminPanel) {
                 public AbstractComposite createInstance() {
                     return new ArtifactTypeForm(adminPanel, at, false);
                 }
             };
             adminPanel.addPage(page);
             
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
