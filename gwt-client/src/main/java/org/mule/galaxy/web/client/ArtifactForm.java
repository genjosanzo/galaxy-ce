package org.mule.galaxy.web.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.util.WorkspacesListBox;

public class ArtifactForm extends AbstractTitledComposite {
    private TextBox nameBox;
    private Grid grid;
    private FormPanel form;
    private FileUpload artifactUpload;
    private TextBox versionBox;
    private WorkspacesListBox workspacesLB;

    public ArtifactForm(final RegistryPanel registryPanel) {
        this(registryPanel, null, true);
    }
    
    public ArtifactForm(final RegistryPanel registryPanel, String artifactId) {
        this(registryPanel, artifactId, false);
    }
    
    protected ArtifactForm(final RegistryPanel registryPanel, String artifactId, boolean add) {
        super();
        form = new FormPanel();
        form.setAction("/artifactUpload");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FlowPanel panel = new FlowPanel();
        form.add(panel);
        
        grid = new Grid(5, 2);
        panel.add(grid);
        
        int row = 0;
        if (add) {
            row = setupAddForm(registryPanel);
        } else {
            row = setupAddVersionForm(registryPanel, panel, artifactId);
        }

        artifactUpload = new FileUpload();
        artifactUpload.setName("artifactFile");
        grid.setWidget(row, 1, artifactUpload);

        grid.setWidget(row+1, 1, new Button("Add", new ClickListener() {
            public void onClick(Widget sender) {
                form.submit();
            }
        }));

        form.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent event) {
                if (artifactUpload.getFilename().length() == 0) {
                    Window.alert("You did not specify a script filename!");
                    event.setCancelled(true);
                }
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                String msg = event.getResults();
                if ("OK".equals(msg)) {
                    registryPanel.setMain(new WorkspacePanel(registryPanel));
                } else {
                    registryPanel.setMessage(msg);
                }
            }
        });

        initWidget(form);

        if (add) {
            setTitle("Add Artifact");
        } else {
            setTitle("Add New Artifact Version");
        }
    }

    private int setupAddForm(final RegistryPanel registryPanel) {
        grid.setWidget(0, 0, new Label("Workspace"));

        workspacesLB = new WorkspacesListBox(registryPanel.getWorkspaces(),
                                             null,
                                             registryPanel.getWorkspaceId(),
                                             false);
        workspacesLB.setName("workspaceId");
        grid.setWidget(0, 1, workspacesLB);
        
        Label nameLabel = new Label("Artifact Name");
        grid.setWidget(1, 0, nameLabel);

        nameBox = new TextBox();
        nameBox.setName("name");
        grid.setWidget(1, 1, nameBox);

        Label versionLabel = new Label("Version Label");
        grid.setWidget(2, 0, versionLabel);

        versionBox = new TextBox();
        grid.setWidget(2, 1, versionBox);
        versionBox.setName("versionLabel");
        
        Label artifactLabel = new Label("Artifact");
        grid.setWidget(3, 0, artifactLabel);
        
        return 3;
    }

    private int setupAddVersionForm(final RegistryPanel registryPanel, FlowPanel panel, String artifactId) {
        Label versionLabel = new Label("Version Label");
        grid.setWidget(0, 0, versionLabel);

        versionBox = new TextBox();
        grid.setWidget(0, 1, versionBox);
        versionBox.setName("versionLabel");
        
        Label artifactLabel = new Label("Artifact");
        grid.setWidget(1, 0, artifactLabel);
        
        panel.add(new Hidden("artifactId", artifactId));
        
        return 1;
    }
}
