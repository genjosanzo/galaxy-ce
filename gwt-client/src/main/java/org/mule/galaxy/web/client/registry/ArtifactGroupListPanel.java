package org.mule.galaxy.web.client.registry;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.artifact.ArtifactPanel;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.BasicArtifactInfo;

/**
 * Lists a group of artifacts.
 */
public class ArtifactGroupListPanel
    extends AbstractComposite
{
    private ArtifactGroup group;

    public ArtifactGroupListPanel(final ArtifactGroup group) {
        super();
        this.group = group;
        
        FlexTable table = createRowTable();
        
        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(0, i, (String) group.getColumns().get(i));
        }
        
        for (int i = 0; i < group.getRows().size(); i++) {
            final BasicArtifactInfo info = (BasicArtifactInfo) group.getRows().get(i);
            for (int c = 0; c < group.getColumns().size(); c++) {
                if (c == 0) {
                    Hyperlink hl = new Hyperlink(info.getValue(c), "artifact/" + info.getId());
//                    MenuPanelPageInfo page = new MenuPanelPageInfo(hl.getTargetHistoryToken(), registryPanel) {
//                        public AbstractComposite createInstance() {
//                            return new ArtifactPanel(registryPanel, info.getId());
//                        }
//                    };
//                    registryPanel.addPage(page);
                    
                    table.setWidget(i+1, c, hl);
                } else {
                    table.setText(i+1, c, info.getValue(c));
                }
                table.getRowFormatter().setStyleName(i+1, "artifactTableEntry");
            }
        }
        
        initWidget(table);
    }

    public String getTitle()
    {
        return group.getName();
    }
}
