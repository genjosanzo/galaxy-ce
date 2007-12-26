package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import org.mule.galaxy.web.client.util.InlineFlowPanel;

public class AddWorkspacePanel extends TitledComposite {

    private TextBox workspaceTextBox;

    public AddWorkspacePanel(String workspace, String workspaceId) {
        super();
        
        FlowPanel panel = new FlowPanel();
        FlexTable table = new FlexTable();
        table.setStyleName("artifactTable");
        table.setCellSpacing(1);
        table.setCellPadding(0);
        table.getColumnFormatter().setStyleName(0, "artifactTableHeader");
        table.getColumnFormatter().setStyleName(1, "artifactTableEntry");
        
        table.setText(0, 0, "Parent Workspace:");
        table.setText(0, 1, workspace);
        
        table.setText(1, 0, "Workspace Name:");
        
        workspaceTextBox = new TextBox();
        table.setWidget(1, 1, workspaceTextBox);
        
        panel.add(table);
        
        InlineFlowPanel buttonPanel = new InlineFlowPanel();
        Button saveButton = new Button("Save");
        buttonPanel.add(saveButton);
        
        initWidget(panel);
        
        setTitle("Add Workspace");
    }

}
