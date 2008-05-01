package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import org.mule.galaxy.web.client.util.InlineFlowPanel;

public abstract class AbstractComposite extends Composite {

    public void onShow() {
        
    }
    
    public void onHide() {
        
    }

    protected Widget asDiv(Widget w) {
        FlowPanel p = new FlowPanel();
        p.add(w);
        return p;
    }
    
    protected Label newLabel(String name, String style) {
        Label label = new Label(name);
        label.setStyleName(style);
        return label;
    }
    protected InlineFlowPanel asHorizontal(Widget w1, Widget w2) {
        InlineFlowPanel p = new InlineFlowPanel();
        p.add(w1);
        p.add(w2);
        return p;
    }

    protected InlineFlowPanel asHorizontal(Widget w1, Widget w2, Widget w3) {
        InlineFlowPanel p = new InlineFlowPanel();
        p.add(w1);
        p.add(w2);
        p.add(w3);
        return p;
    }
    
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
        FlexTable table = new FlexTable();
        table.getRowFormatter().setStyleName(0, "artifactTableHeader");
        table.setStyleName("artifactTableFull");
        table.setWidth("100%");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        
        return table;
    }

    protected void styleHeaderColumn(FlexTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.getCellFormatter().setStyleName(i, 0, "artifactTableHeader");
            table.getCellFormatter().setStyleName(i, 1, "artifactTableEntry");
        }
    }

    protected FlexTable createColumnTable() {
        FlexTable table = createTable();
        table.setStyleName("columnTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        
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

    
    protected InlineFlowPanel createTitleWithLink(String name, Widget rightWidget) {
        InlineFlowPanel commentTitlePanel = new InlineFlowPanel();
        commentTitlePanel.setStyleName("rightlinked-title-panel");

        commentTitlePanel.add(rightWidget);
        
        Label label = new Label(name);
        label.setStyleName("rightlinked-title");
        commentTitlePanel.add(label);
        
        rightWidget.setStyleName("rightlinked-title-link");
        return commentTitlePanel;
    }
}
