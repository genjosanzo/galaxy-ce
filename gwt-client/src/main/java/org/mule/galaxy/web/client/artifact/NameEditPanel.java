package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.WorkspacesListBox;
import org.mule.galaxy.web.rpc.AbstractCallback;

public class NameEditPanel extends Composite {

    private InlineFlowPanel panel;
    private final String artifactId;
    private final String name;
    private final String workspaceId;
    private final Galaxy galaxy;
    private final ErrorPanel errorPanel;

    public NameEditPanel(Galaxy galaxy, 
                         ErrorPanel errorPanel, 
                         String artifactId, 
                         String name, 
                         String workspaceId) {
        super();
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
        this.artifactId = artifactId;
        this.name = name;
        this.workspaceId = workspaceId;

        panel = new InlineFlowPanel();
       
        initName();
        
        initWidget(panel);
    }

    private void initName() {
        panel.add(new Label(name + " "));

        Hyperlink editHL = new Hyperlink("Edit", "edit-property");
        editHL.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                showEditPanel();
            }
            
        });
        panel.add(editHL);
    }

    protected void showEditPanel() {
        panel.clear();
        panel.add(new Label("Loading workspaces..."));
        
        galaxy.getRegistryService().getWorkspaces(new AbstractCallback(errorPanel) {
            public void onSuccess(Object workspaces) {
                showEditPanel((Collection) workspaces);
            }
        });
    }

    protected void showEditPanel(Collection workspaces) {
        panel.clear();
        
        
        final WorkspacesListBox workspacesLB = new WorkspacesListBox(workspaces, 
                                                                     null,
                                                                     workspaceId,
                                                                     false);
        panel.add(workspacesLB);
        
        final   TextBox nameTB = new TextBox();
        nameTB.setText(name);
        panel.add(nameTB);
        
        Button saveButton = new Button("Save");
        saveButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                save(workspacesLB.getSelectedValue(), nameTB.getText());
            }
            
        });
        panel.add(saveButton);
        
        Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(new ClickListener() {

            public void onClick(Widget arg0) {
                panel.clear();
                initName();
            }
            
        });
        panel.add(cancelButton);
    }

    protected void save(String workspaceId, String name) {
        galaxy.getRegistryService().move(artifactId, workspaceId, name, new AbstractCallback(errorPanel) {

            public void onSuccess(Object arg0) {
                panel.clear();
                initName();
            }
            
        });
    }

}
