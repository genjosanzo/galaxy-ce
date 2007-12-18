package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

public class ArtifactListPanel
    extends Composite
{
    public ArtifactListPanel() {
        super();
        
        FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        table.setText(0, 0, "Name");
        table.setText(0, 1, "Namespace");
        table.setText(0, 2, "Services");
        table.setText(0, 3, "Endpoints");
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
        
        table.setText(1, 0, "GoogleSearch");
        table.setText(1, 1, "urn:GoogleSearch");
        table.setText(1, 2, "1");
        table.setText(1, 3, "1");
        
        table.setText(2, 0, "FooService");
        table.setText(2, 1, "urn:FooBar");
        table.setText(2, 2, "4");
        table.setText(2, 3, "2");
        
        initWidget(table);
    }

    public String getTitle()
    {
        return "WSDL";
    }
}
