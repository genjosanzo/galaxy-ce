package org.mule.galaxy.web.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

import org.mule.galaxy.web.client.artifact.ArtifactPanel;
import org.mule.galaxy.web.client.artifact.ArtifactPolicyResultsPanel;
import org.mule.galaxy.web.client.util.WorkspacesListBox;

public class ArtifactForm extends AbstractTitledComposite {
    private TextBox nameBox;
    private FlexTable table;
    private FormPanel form;
    private FileUpload artifactUpload;
    private TextBox versionBox;
    private WorkspacesListBox workspacesLB;
    private final RegistryPanel registryPanel;
    private final String artifactId;

    public ArtifactForm(final RegistryPanel registryPanel) {
        this(registryPanel, null, true);
    }
    
    public ArtifactForm(final RegistryPanel registryPanel, String artifactId) {
        this(registryPanel, artifactId, false);
    }
    
    protected ArtifactForm(final RegistryPanel registryPanel, 
                           final String artifactId, 
                           final boolean add) {
        super();
        this.registryPanel = registryPanel;
        this.artifactId = artifactId;
        form = new FormPanel();
        form.setAction("/artifactUpload");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        FlowPanel panel = new FlowPanel();
        form.add(panel);
        
        table = createColumnTable();
        panel.add(table);
        
        int row = 0;
        if (add) {
            row = setupAddForm(registryPanel);
        } else {
            row = setupAddVersionForm(registryPanel, panel);
        }

        artifactUpload = new FileUpload();
        artifactUpload.setName("artifactFile");
        table.setWidget(row, 1, artifactUpload);

        table.setWidget(row+1, 1, new Button("Add", new ClickListener() {
            public void onClick(Widget sender) {
                form.submit();
            }
        }));

        form.addFormHandler(new FormHandler() {
            public void onSubmit(FormSubmitEvent event) {
                if (artifactUpload.getFilename().length() == 0) {
                    Window.alert("You did not specify a filename!");
                    event.setCancelled(true);
                }
                
                String version = versionBox.getText();
                if (version == null || "".equals(version)) {
                    registryPanel.setMessage("You must specify a version label.");
                    event.setCancelled(true);
                }
            }

            public void onSubmitComplete(FormSubmitCompleteEvent event) {
                String msg = event.getResults();
                registryPanel.setMessage(msg);
                if (msg.startsWith("<PRE>OK ")) {
                    int last = msg.indexOf("</PRE>");
                    if (last == -1) last = msg.length();
                    
                    if (add) {
                        registryPanel.setMain(new ArtifactPanel(registryPanel, msg.substring(8, last)));
                    } else {
                        registryPanel.setMain(new ArtifactPanel(registryPanel, artifactId));
                    }
                } else if (msg.startsWith("<PRE>ArtifactPolicyException")) {
                    parseAndShowPolicyMessages(msg);
                } else {
                    registryPanel.setMessage(msg);
                }
            }
        });
        
        styleHeaderColumn(table);

        initWidget(form);

        if (add) {
            setTitle("Add Artifact");
        } else {
            setTitle("Add New Artifact Version");
        }
    }

    protected void parseAndShowPolicyMessages(String msg) {
        String[] split = msg.split("\n");
        
        List warnings = new ArrayList();
        List failures = new ArrayList();
        for (int i = 1; i < split.length; i++) {
            String s = split[i];
            
            if (s.startsWith("WARNING: ")) {
                warnings.add(getMessage(s));
            } else if (s.startsWith("FAILURE: ")) {
                failures.add(getMessage(s));
            }
        }
        
        registryPanel.setMain(new ArtifactPolicyResultsPanel(warnings, failures));
        registryPanel.setMessage("The artifact did not meet all the necessary policies!");
    }

    private String getMessage(String s) {
        s = s.substring(9);
        if (s.endsWith("</PRE>")) {
            s = s.substring(0, s.length() - 6);
        }
        return s;
    }

    private int setupAddForm(final RegistryPanel registryPanel) {
        table.setWidget(0, 0, new Label("Workspace"));

        workspacesLB = new WorkspacesListBox(registryPanel.getWorkspaces(),
                                             null,
                                             registryPanel.getWorkspaceId(),
                                             false);
        workspacesLB.setName("workspaceId");
        table.setWidget(0, 1, workspacesLB);
        
        Label nameLabel = new Label("Artifact Name");
        table.setWidget(1, 0, nameLabel);

        nameBox = new TextBox();
        nameBox.setName("name");
        table.setWidget(1, 1, nameBox);

        Label versionLabel = new Label("Version Label");
        table.setWidget(2, 0, versionLabel);

        versionBox = new TextBox();
        table.setWidget(2, 1, versionBox);
        versionBox.setName("versionLabel");
        
        Label artifactLabel = new Label("Artifact");
        table.setWidget(3, 0, artifactLabel);
        
        return 3;
    }

    private int setupAddVersionForm(final RegistryPanel registryPanel, FlowPanel panel) {
        Label versionLabel = new Label("Version Label");
        table.setWidget(0, 0, versionLabel);

        versionBox = new TextBox();
        table.setWidget(0, 1, versionBox);
        versionBox.setName("versionLabel");
        
        Label artifactLabel = new Label("Artifact");
        table.setWidget(1, 0, artifactLabel);
        
        panel.add(new Hidden("artifactId", artifactId));
        
        return 1;
    }
}
