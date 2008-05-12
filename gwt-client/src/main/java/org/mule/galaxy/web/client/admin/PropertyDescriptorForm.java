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

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private TextBox nameTB;
    private TextBox descriptionTB;

    public PropertyDescriptorForm(AdministrationPanel adminPanel){
        super(adminPanel, "property-descriptors", "Property was saved.", "Property was deleted.");
    }
    
    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Description:");
//        table.setText(2, 0, "Multivalued");
        
        nameTB = new TextBox();
        nameTB.setText(property.getName());
        table.setWidget(0, 1, nameTB);
        
        descriptionTB = new TextBox();
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);

        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
//      adminPanel.getRegistryService().getPropertyDescriptors(abstractCallback)
    }

    public String getTitle() {
        if (newItem) {
            return "Add Property";
        } else {
            return "Edit Property Descriptor: " + property.getName();
        }
    }

    protected void initializeItem(Object o) {
        this.property = (WPropertyDescriptor) o;
    }

    protected void initializeNewItem() {
        this.property = new WPropertyDescriptor();
    }

    protected void save() {
        super.save();
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        property.setDescription(descriptionTB.getText());
        property.setName(nameTB.getText());
        
        svc.savePropertyDescriptor(property, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        svc.deletePropertyDescriptor(property.getId(), getDeleteCallback());
    }
    
}
