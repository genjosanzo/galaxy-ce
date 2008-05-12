package org.mule.galaxy.web.client.admin;

import java.util.List;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.util.DeleteDialog;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.QNameListBox;
import org.mule.galaxy.web.client.util.DeleteDialog.DeleteListener;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WArtifactType;

public class ArtifactTypeForm extends AbstractAdministrationForm {

    private WArtifactType artifactType;
    private TextBox descriptionTB;
    private QNameListBox docTypesLB;
    private TextBox mediaTypeTB;
    
    public ArtifactTypeForm(AdministrationPanel adminPanel){
        super(adminPanel, 
              "artifact-types", 
              "Artifact type was saved.", 
              "Artifact type was deleted.");
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Description:");
        table.setText(1, 0, "Media Type:");
        table.setText(2, 0, "Document Types:");
        
        descriptionTB = new TextBox();
        descriptionTB.setText(artifactType.getDescription());
        table.setWidget(0, 1, descriptionTB);
        
        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(artifactType.getMediaType());
        table.setWidget(1, 1, mediaTypeTB);
        
        docTypesLB = new QNameListBox(artifactType.getDocumentTypes());
        table.setWidget(2, 1, docTypesLB);
        
        styleHeaderColumn(table);
    }

    public String getTitle() {
        if (newItem) {
            return "Add Artifact Type";
        } else {
            return "Edit Artifact Type" ;
        }
    }

    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getArtifactType(id, getFetchCallback());
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
        
        adminPanel.getRegistryService().saveArtifactType(artifactType, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        adminPanel.getRegistryService().deleteArtifactType(artifactType.getId(), getDeleteCallback());
    }
    
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        
        docTypesLB.setEnabled(enabled);
    }
}
