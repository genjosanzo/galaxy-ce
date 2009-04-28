/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.mule.galaxy.web.client.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.rpc.WType;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;

public class TypeForm extends AbstractAdministrationForm {

    private WType type;
    private ValidatableTextBox nameTB;
    private ListBox propertiesLB;
    private ListBox mixinsLB;
    private ListBox childrenLB;
    private Map<String, WType> types = new HashMap<String, WType>();
    private Map<String, WPropertyDescriptor> properties = new HashMap<String, WPropertyDescriptor>();

    public TypeForm(AdministrationPanel adminPanel){
        super(adminPanel, "types", "Type was saved.", "Type was deleted.", 
              "A type with that name already exists.");
    }
    
    protected void addFields(final FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Properties:");
        table.setText(2, 0, "Mixins:");
        table.setText(3, 0, "Allowed Children:");
        
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(type.getName());

        table.setWidget(0, 1, nameTB);
        
        propertiesLB = new ListBox();
        propertiesLB.setVisibleItemCount(8);
        propertiesLB.setMultipleSelect(true);
        table.setWidget(1, 1, propertiesLB);
        
        mixinsLB = new ListBox();
        mixinsLB.setVisibleItemCount(6);
        mixinsLB.setMultipleSelect(true);
        table.setWidget(2, 1, mixinsLB);

        childrenLB = new ListBox();
        childrenLB.setVisibleItemCount(6);
        childrenLB.setMultipleSelect(true);
        table.setWidget(3, 1, childrenLB);
        
        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getType(id, getFetchCallback());
       
    }

    protected void initializeProperties(List<WPropertyDescriptor> pds) {
        for (WPropertyDescriptor pd : pds) {
            properties.put(pd.getName(), pd);
            propertiesLB.addItem(pd.getDescription(), pd.getId());
            if (type.getProperties() != null && type.getProperties().contains(pd)) {
                propertiesLB.setItemSelected(propertiesLB.getItemCount()-1, true);
            }
        }
    }

    protected void initializeTypes(List<WType> wts) {
        for (WType type : wts) {
            types.put(type.getId(), type);
            mixinsLB.addItem(type.getName(), type.getId());
            if (this.type.getMixinIds().contains(type.getId())) {
                mixinsLB.setItemSelected(mixinsLB.getItemCount()-1, true);
            }
            childrenLB.addItem(type.getName(), type.getId());
            if (this.type.getAllowedChildrenIds().contains(type.getId())) {
                childrenLB.setItemSelected(mixinsLB.getItemCount()-1, true);
            }
        }
    }

    public String getTitle() {
        if (newItem) {
            return "Add Property";
        } else {
            return "Edit Property Descriptor: " + type.getName();
        }
    }

    protected void initializeItem(Object o) {
        this.type = (WType) o;
        intializeTypesAndProperties();
    }

    protected void initializeNewItem() {
        this.type = new WType();
        intializeTypesAndProperties();
    }

    protected void intializeTypesAndProperties() {
        adminPanel.getRegistryService().getTypes(new AbstractCallback<List<WType>>(errorPanel) {
            public void onSuccess(List<WType> types) {
                initializeTypes(types);
            }

            @Override
            public void onFailure(Throwable caught) {
                errorPanel.setMessage(caught.getClass().getName());
                super.onFailure(caught);
            }
        });

        adminPanel.getRegistryService().getPropertyDescriptors(false, new AbstractCallback(errorPanel) {
            public void onSuccess(Object pds) {
                initializeProperties((List<WPropertyDescriptor>) pds);
            }

            @Override
            public void onFailure(Throwable caught) {
                errorPanel.setMessage(caught.getClass().getName());
                super.onFailure(caught);
            }
        });
    }
    
    protected void save() {

        if (!validate()) {
            return;
        }

        RegistryServiceAsync svc = adminPanel.getRegistryService();

        type.setName(nameTB.getTextBox().getText());
        type.setProperties(new ArrayList<WPropertyDescriptor>());
        type.setAllowedChildrenIds(new ArrayList<String>());
        type.setMixinIds(new ArrayList<String>());
        
        for (int i = 0; i < propertiesLB.getItemCount(); i++) {
            if (propertiesLB.isItemSelected(i)) {
                type.getProperties().add(properties.get(propertiesLB.getValue(i)));
            }
        }
        
        for (int i = 0; i < childrenLB.getItemCount(); i++) {
            if (childrenLB.isItemSelected(i)) {
                type.getAllowedChildrenIds().add(childrenLB.getValue(i));
            }
        }

        for (int i = 0; i < mixinsLB.getItemCount(); i++) {
            if (mixinsLB.isItemSelected(i)) {
                type.getMixinIds().add(mixinsLB.getValue(i));
            }
        }
        svc.saveType(type, getSaveCallback());
    }

    protected void delete() {
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                TypeForm.super.delete();
                RegistryServiceAsync svc = adminPanel.getRegistryService();
                svc.deletePropertyDescriptor(type.getId(), getDeleteCallback());
            }
        }, "Are you sure you want to delete type " + type.getName() + "?");
        new LightBox(dialog).show();
    }

    protected boolean validate() {
        boolean isOk = true;

        isOk &= nameTB.validate();
        
        return isOk;
    }

}
