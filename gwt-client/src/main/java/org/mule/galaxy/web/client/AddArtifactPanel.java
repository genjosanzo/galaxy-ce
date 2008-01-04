package org.mule.galaxy.web.client;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddArtifactPanel extends AbstractTitledComposite {
    private TextBox nameBox;
    private Grid grid;
    private FormPanel form;
    private FileUpload artifactUpload;
    private TextBox versionBox;

    public AddArtifactPanel(final RegistryPanel registryPanel) {
        super();
        form = new FormPanel();
        form.setAction("/artifactUpload");
        form.setEncoding(FormPanel.ENCODING_MULTIPART);
        form.setMethod(FormPanel.METHOD_POST);

        grid = new Grid(4, 2);
        form.add(grid);

        Label nameLabel = new Label("Artifact Name");
        grid.setWidget(0, 0, nameLabel);

        nameBox = new TextBox();
        nameBox.setName("name");
        grid.setWidget(0, 1, nameBox);

        Label versionLabel = new Label("Version Label");
        grid.setWidget(1, 0, versionLabel);

        versionBox = new TextBox();
        grid.setWidget(1, 1, versionBox);
        versionBox.setName("versionLabel");
        
        Label artifactLabel = new Label("Artifact");
        grid.setWidget(2, 0, artifactLabel);

        artifactUpload = new FileUpload();
        artifactUpload.setName("artifactFile");
        grid.setWidget(2, 1, artifactUpload);

        grid.setWidget(3, 1, new Button("Submit", new ClickListener() {
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
                registryPanel.setMessage(event.getResults());
            }
        });

        initWidget(form);

        setTitle("Add Artifact");
    }

}
