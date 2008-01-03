package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.artifact.ArtifactPanel;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.BasicArtifactInfo;

public class ArtifactListPanel
    extends Composite
{
    private ArtifactGroup group;

    public ArtifactListPanel(final RegistryPanel registryPanel, 
                             final ArtifactGroup group) {
        super();
        this.group = group;
        
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(0, i, (String) group.getColumns().get(i));
        }
        
        table.getRowFormatter().setStyleName(0, "artifactTableHeader");
        
        for (int i = 0; i < group.getRows().size(); i++) {
            final BasicArtifactInfo info = (BasicArtifactInfo) group.getRows().get(i);
            for (int c = 0; c < group.getColumns().size(); c++) {
                if (c == 0) {
                    Hyperlink hl = new Hyperlink(info.getValue(c), "artifact-" + info.getId());
                    hl.addClickListener(new ClickListener() {

                        public void onClick(Widget arg0) {
                            registryPanel.setMain(new ArtifactPanel(registryPanel, info.getId()));
                        }
                        
                    });
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
