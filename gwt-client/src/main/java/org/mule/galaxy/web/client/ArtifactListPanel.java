package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class ArtifactListPanel
    extends Composite
{
    private ArtifactGroup group;

    public ArtifactListPanel(ArtifactGroup group) {
        super();
        this.group = group;
        
        FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        for (int i = 0; i < group.getColumns().size(); i++) {
            table.setText(0, i, (String) group.getColumns().get(i));
        }
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
        
        for (int i = 0; i < group.getRows().size(); i++) {
            BasicArtifactInfo info = (BasicArtifactInfo) group.getRows().get(i);
            for (int c = 0; c < group.getColumns().size(); c++) {
                table.setText(i+1, c, info.getValue(c));
            }
        }
        
        initWidget(table);
    }

    public String getTitle()
    {
        return group.getName();
    }
}
