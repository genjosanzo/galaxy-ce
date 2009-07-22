package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;

import org.mule.galaxy.web.client.util.InlineFlowPanel;

public class WidgetHelper extends Composite {

    public static Label newSpacer() {
        Label spacer = new Label(" ");
        spacer.setStyleName("spacer");
        return spacer;
    }

    public static Label newSpacerPipe() {
        Label pipe = new Label(" | ");
        pipe.setStyleName("pipe-with-space");
        return pipe;
    }

    public static Widget asDiv(Widget w) {
        FlowPanel p = new FlowPanel();
        p.add(w);
        return p;
    }

    public static Label newLabel(String name, String style) {
        Label label = new Label(name);
        label.setStyleName(style);
        return label;
    }

    public static InlineFlowPanel asHorizontal(Widget w1, Widget w2) {
        InlineFlowPanel p = new InlineFlowPanel();
        p.add(w1);
        p.add(w2);
        return p;
    }

    public static InlineFlowPanel asHorizontal(Widget w1, Widget w2, Widget w3) {
        InlineFlowPanel p = new InlineFlowPanel();
        p.add(w1);
        p.add(w2);
        p.add(w3);
        return p;
    }

    public static FlexTable createTitledRowTable(Panel panel, String title) {
        panel.add(createPrimaryTitle(title));
        FlexTable table = createRowTable();
        panel.add(table);
        return table;
    }

    public static FlexTable createTitledColumnTable(Panel panel, String title) {
        panel.add(createTitle(title));
        FlexTable table = createColumnTable();
        panel.add(table);
        return table;
    }

    public static FlexTable createRowTable() {
        FlexTable table = new FlexTable();
        table.getRowFormatter().setStyleName(0, "artifactTableHeader");
        table.setStyleName("artifactTableFull");
        table.setWidth("100%");
        table.setCellSpacing(0);
        table.setCellPadding(0);
    
        return table;
    }

    public static void styleHeaderColumn(FlexTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            table.getCellFormatter().setStyleName(i, 0, "artifactTableHeader");
            table.getCellFormatter().setStyleName(i, 1, "artifactTableEntry");
        }
    }

    public static FlexTable createColumnTable() {
        FlexTable table = createTable();
        table.setStyleName("columnTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
    
        return table;
    }

    public static FlexTable createTable() {
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        return table;
    }

    public static Widget createPrimaryTitle(String title) {
        Label label = new Label(title);
        label.setStyleName("title");
        return label;
    }

    public static InlineFlowPanel createTitle(String title) {
        InlineFlowPanel titlePanel = new InlineFlowPanel();
        titlePanel.setStyleName("rightlinked-title-panel");
    
        Label label = new Label(title);
        label.setStyleName("rightlinked-title");
        titlePanel.add(label);
        return titlePanel;
    }

    public static Label createTitleText(String title) {
        Label label = new Label(title);
        label.setStyleName("right-title");
        return label;
    }

    public static InlineFlowPanel createTitleWithLink(String name, Widget rightWidget) {
        InlineFlowPanel commentTitlePanel = new InlineFlowPanel();
        commentTitlePanel.setStyleName("rightlinked-title-panel");
    
        commentTitlePanel.add(rightWidget);
    
        Label label = new Label(name);
        label.setStyleName("rightlinked-title");
        commentTitlePanel.add(label);
    
        rightWidget.setStyleName("rightlinked-title-link");
        return commentTitlePanel;
    }

    public static ContentPanel createInlineHelpPanel(String header, String body) {
        ContentPanel cp = new ContentPanel();
        cp.addText(body);
        cp.setHeading(header);
        cp.setBorders(false);
        cp.setTitleCollapse(true);
        cp.setCollapsible(true);
        cp.setAutoWidth(true);
        cp.collapse();
        cp.setStyleName("help-panel-inline");
        cp.setBodyStyleName("help-panel-inline");
        cp.getHeader().setStyleName("help-panel-inline");
        cp.getHeader().setTextStyle("help-panel-inline");
        return cp;
    }

    public static ContentPanel createAccodionWrapperPanel() {
        AccordionLayout alayout = new AccordionLayout();
        alayout.setHideCollapseTool(true);
        alayout.setFill(true);
        ContentPanel accordionPanel = new ContentPanel();
        accordionPanel.setBodyBorder(false);
        accordionPanel.setHeaderVisible(false);
        accordionPanel.setLayout(alayout);
        return accordionPanel;
    }



    public WidgetHelper() {
        super();
    }

}
