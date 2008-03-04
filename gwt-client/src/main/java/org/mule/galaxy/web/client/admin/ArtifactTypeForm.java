package org.mule.galaxy.web.client.admin;

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

public class ArtifactTypeForm extends AbstractComposite {

    private AdministrationPanel adminPanel;
    private WArtifactType artifactType;
    private Button save;
    private TextBox descriptionTB;
    private final boolean add;
    private FlowPanel panel;
    private Button delete;
    private QNameListBox docTypesLB;
    private TextBox mediaTypeTB;

    public ArtifactTypeForm(AdministrationPanel adminPanel, WArtifactType u) {
        this (adminPanel, u, false);
    }
    
    public ArtifactTypeForm(AdministrationPanel adminPanel) {
        this (adminPanel, new WArtifactType(), true);
    }
    
    protected ArtifactTypeForm(AdministrationPanel adminPanel, WArtifactType u, boolean add){
        this.adminPanel = adminPanel;
        this.artifactType = u;
        this.add = add;
        
        panel = new FlowPanel();
        initWidget(panel);
    }
    
    public void onShow() {
        panel.clear();
        
        String title;
        if (add) {
            title = "Add Artifact Type";
        } else {
            title = "Edit Artifact Type" ;
        }
        
        final FlexTable table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Description");
        table.setText(1, 0, "Media Type");
        table.setText(2, 0, "Document Types");
        
        descriptionTB = new TextBox();
        descriptionTB.setText(artifactType.getDescription());
        table.setWidget(0, 1, descriptionTB);
        
        mediaTypeTB = new TextBox();
        mediaTypeTB.setText(artifactType.getMediaType());
        table.setWidget(1, 1, mediaTypeTB);
        
        docTypesLB = new QNameListBox(artifactType.getDocumentTypes());
        table.setWidget(2, 1, docTypesLB);

        save = new Button("Save");
        save.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                save();
            }
            
        });
        
        if (add) {
            table.setWidget(3, 1, save);
        } else {
            InlineFlowPanel buttons = new InlineFlowPanel();
            buttons.add(save);
            
            final DeleteDialog popup = new DeleteDialog("artifact type", new DeleteListener() {
                public void onYes() {
                    delete();
                }
            });
            
            delete = new Button("Delete");
            delete.addClickListener(new ClickListener() {
                public void onClick(Widget sender) {
                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (Window.getClientWidth() - offsetWidth) / 3;
                            int top = (Window.getClientHeight() - offsetHeight) / 3;
                            popup.setPopupPosition(left, top);
                        }
                    });
                }
            });
            buttons.add(delete);
            
            table.setWidget(3, 1, buttons);
            
        }
        
        styleHeaderColumn(table);
    }

    protected void save() {
        disable();
        save.setText("Saving...");

        artifactType.setMediaType(mediaTypeTB.getText());
        artifactType.setDocumentTypes(docTypesLB.getItems());
        artifactType.setDescription(descriptionTB.getText());
        
        adminPanel.getRegistryService().saveArtifactType(artifactType, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                History.newItem("artifact-types");
            }
            
        });
    }

    protected void delete() {
        disable();
        delete.setText("Deleting...");
        
        adminPanel.getRegistryService().deleteArtifactType(artifactType.getId(), new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                reenable();
                super.onFailure(caught);
            }

            public void onSuccess(Object arg0) {
                History.newItem("artifact-types");
            }
            
        });
    }
    
    public void disable() {
        save.setEnabled(false);
        delete.setEnabled(false);
        docTypesLB.setEnabled(false);
    }
    
    public void reenable() {
        save.setEnabled(true);
        save.setText("Save");
        delete.setEnabled(true);
        delete.setText("Delete");
        docTypesLB.setEnabled(true);
    }
}
