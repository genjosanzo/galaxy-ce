package org.mule.galaxy.repository.client.admin;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WArtifactType;
import org.mule.galaxy.web.client.admin.AbstractAdministrationForm;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.field.QNameListBox;
import org.mule.galaxy.web.client.ui.field.StringListBox;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

public class ArtifactTypeForm extends AbstractAdministrationForm {

    private WArtifactType artifactType;
    private TextBox descriptionTB;
    private QNameListBox docTypesLB;
    private TextBox mediaTypeTB;
    private StringListBox extsLB;
    private final RegistryServiceAsync registryService;

    public ArtifactTypeForm(AdministrationPanel adminPanel, RegistryServiceAsync registryService) {
        super(adminPanel,
                "artifact-types",
                "Artifact type was saved.",
                "Artifact type was deleted.", null);
        this.registryService = registryService;
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Description:");
        table.setText(1, 0, "Media Type:");
        table.setText(2, 0, "Document Types:");
        table.setText(3, 0, "File Extensions:");

        descriptionTB = new TextBox();
        descriptionTB.setText(artifactType.getDescription());
        table.setWidget(0, 1, descriptionTB);

        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(artifactType.getMediaType());
        table.setWidget(1, 1, mediaTypeTB);

        docTypesLB = new QNameListBox(artifactType.getDocumentTypes());
        table.setWidget(2, 1, docTypesLB);

        extsLB = new StringListBox(artifactType.getFileExtensions());
        table.setWidget(3, 1, extsLB);

        styleHeaderColumn(table);
    }

    public String getTitle() {
        if (newItem) {
            return "Add Artifact Type";
        } else {
            return "Edit Artifact Type";
        }
    }

    protected void fetchItem(String id) {
        registryService.getArtifactType(id, getFetchCallback());
    }

    protected void initializeItem(Object o) {
        this.artifactType = (WArtifactType) o;
    }

    protected void initializeNewItem() {
        this.artifactType = new WArtifactType();
    }

    protected void save() {
        super.save();

        artifactType.setMediaType(mediaTypeTB.getText());
        artifactType.setDocumentTypes(docTypesLB.getItems());
        artifactType.setDescription(descriptionTB.getText());
        artifactType.setFileExtensions(extsLB.getItems());

        registryService.saveArtifactType(artifactType, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    ArtifactTypeForm.super.delete();
                    registryService.deleteArtifactType(artifactType.getId(), getDeleteCallback());
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete artifact type " + artifactType.getDescription() + "?", l);
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        docTypesLB.setEnabled(enabled);
    }


}
