package org.mule.galaxy.web.client.admin;

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
import org.mule.galaxy.web.client.util.DeleteDialog.DeleteListener;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

public class PropertyDescriptorForm extends AbstractComposite {

    private AdministrationPanel adminPanel;
    private WPropertyDescriptor property;
    private Button save;
    private TextBox nameTB;
    private TextBox descriptionTB;
    private final boolean add;
    private FlowPanel panel;
    private Button delete;

    public PropertyDescriptorForm(AdministrationPanel adminPanel, WPropertyDescriptor u) {
        this (adminPanel, u, false);
    }
    
    public PropertyDescriptorForm(AdministrationPanel adminPanel) {
        this (adminPanel, new WPropertyDescriptor(), true);
    }
    
    protected PropertyDescriptorForm(AdministrationPanel adminPanel, WPropertyDescriptor u, boolean add){
        this.adminPanel = adminPanel;
        this.property = u;
        this.add = add;
        
        panel = new FlowPanel();
        initWidget(panel);
    }
    
    public void onShow() {
        panel.clear();
        
        String title;
        if (add) {
            title = "Add Property";
        } else {
            title = "Edit Property Descriptor: " + property.getName();
        }
        
        final FlexTable table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Description:");
//        table.setText(2, 0, "Multivalued");
        
        nameTB = new TextBox();
        nameTB.setText(property.getName());
        table.setWidget(0, 1, nameTB);
        
        descriptionTB = new TextBox();
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);

        save = new Button("Save");
        save.addClickListener(new ClickListener() {
            public void onClick(Widget sender) {
                save();
            }
        });
        
        if (add) {
            table.setWidget(2, 1, save);
        } else {
            InlineFlowPanel buttons = new InlineFlowPanel();
            buttons.add(save);
            
            final DeleteDialog popup = new DeleteDialog("property", new DeleteListener() {
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
            
            table.setWidget(2, 1, buttons);
            
        }
        
        styleHeaderColumn(table);
    }

    protected void save() {
        setEnabled(true);
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        property.setDescription(descriptionTB.getText());
        property.setName(nameTB.getText());
        
        svc.savePropertyDescriptor(property, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("Property was not found! " + property.getId());
                    setEnabled(true);
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showPropertyDescriptors();
                adminPanel.setMessage("Property " + property.getName() + " was saved.");
            }
            
        });
    }

    private void setEnabled(boolean enabled) {
        save.setEnabled(enabled);
        if (enabled) save.setText("Saving...");
        else save.setText("Save");
        
        if (delete != null) {
            delete.setEnabled(enabled);
            if (enabled) delete.setText("Deleting...");
            else delete.setText("Delete");
        }
    }

    protected void delete() {
        save.setEnabled(false);
        save.setText("Deleting...");
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        svc.deletePropertyDescriptor(property.getId(), new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("User was not found! " + property.getId());
                    setEnabled(true);
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("Property " + property.getName() + " was deleted.");
            }
            
        });
    }
    
}
