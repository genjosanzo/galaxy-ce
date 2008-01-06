package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;

public abstract class AbstractComposite extends Composite {

    protected FlexTable createTitledRowTable(Panel panel, String title) {
        panel.add(createTitle(title));
        FlexTable table = createRowTable();
        panel.add(table);
        return table;
    }

    protected FlexTable createTitledColumnTable(Panel panel, String title) {
        panel.add(createTitle(title));
        FlexTable table = createColumnTable();
        panel.add(table);
        return table;
    }
    
    protected FlexTable createRowTable() {
        FlexTable table = createTable();

        table.setWidth("100%");
        table.getRowFormatter().setStyleName(0, "artifactTableHeader");
        
        return table;
    }

    protected FlexTable createColumnTable() {
        FlexTable table = createTable();
        
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        return table;
    }
    
    protected FlexTable createTable() {
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        return table;
    }

    protected InlineFlowPanel createTitle(String title) {
        InlineFlowPanel titlePanel = new InlineFlowPanel();
        titlePanel.setStyleName("rightlinked-title-panel");
        
        Label label = new Label(title);
        label.setStyleName("rightlinked-title");
        titlePanel.add(label);
        return titlePanel;
    }

}
