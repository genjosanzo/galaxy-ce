/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WExtensionInfo;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private ValidatableTextBox nameTB;
    private TextBox descriptionTB;
    private CheckBox multivalue;
    private ListBox typeLB;
    private HashMap<String, ValidatableTextBox> fields;

    public PropertyDescriptorForm(AdministrationPanel adminPanel){
        super(adminPanel, "properties", "Property was saved.", "Property was deleted.", 
              "A property with that name already exists.");
    }
    
    protected void addFields(final FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Description:");
        table.setText(2, 0, "Type:");
        
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(property.getName());

        table.setWidget(0, 1, nameTB);
        
        descriptionTB = new TextBox();
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);
        
        if (property.getId() == null) {
            addTypeSelector(table);

            showTypeConfiguration(table, "");
        } else {
            String id = "";
            if (property.getExtension() != null) {
                WExtensionInfo ext = adminPanel.getGalaxy().getExtension(property.getExtension());
                id = ext.getId();
                table.setText(2, 1, ext.getDescription());
            } else {
                table.setText(2, 1, "String");
            }

            showTypeConfiguration(table, id);
        } 
        styleHeaderColumn(table);
    }
    private void addTypeSelector(final FlexTable table) {
        typeLB = new ListBox();
        typeLB.addItem("String", "");
        
        List extensions = adminPanel.getGalaxy().getExtensions();
        
        for (Iterator itr = extensions.iterator(); itr.hasNext();) {
            WExtensionInfo e = (WExtensionInfo) itr.next();
            
            typeLB.addItem(e.getDescription(), e.getId());
            
            if (e.getId().equals(property.getExtension())) {
                typeLB.setSelectedIndex(typeLB.getItemCount()-1);
                showTypeConfiguration(table, e.getId());
            }
        }
        
        typeLB.addChangeListener(new ChangeListener() {

            public void onChange(Widget arg0) {
                int idx = typeLB.getSelectedIndex();
                String id;
                if (idx == 0) {
                    id = "";
                } else {
                    id = typeLB.getValue(idx);
                }
                showTypeConfiguration(table, id);
            }
            
        });
        
        table.setWidget(2, 1, typeLB);
    }


    private void showTypeConfiguration(FlexTable table, String id) {
        for (int i = 3; i < table.getRowCount(); i++) {
            table.removeRow(i);
        }

        fields = new HashMap<String, ValidatableTextBox>();
        
        if ("".equals(id)) {
            initializeMultivalue(table);
            styleHeaderColumn(table);
            return;
        }
        
        WExtensionInfo ei = adminPanel.getGalaxy().getExtension(id);
        
        if (ei.getConfigurationKeys() == null) return;
        
        if (ei.isMultivalueSupported()) {
            initializeMultivalue(table);
        }
        
        Map<String, String> config = property.getConfiguration();
        for (Iterator itr = ei.getConfigurationKeys().iterator(); itr.hasNext();) {
            int row = table.getRowCount();
            String key = (String) itr.next();
            
            table.setText(row, 0, key + ":");
            
            ValidatableTextBox field = new ValidatableTextBox(new StringNotEmptyValidator());
            if (config != null) {
                field.getTextBox().setText(config.get(key));
            }
            fields.put(key, field);
            table.setWidget(row, 1, field);
        }
        
        styleHeaderColumn(table);
    }

    private void initializeMultivalue(FlexTable table) {

        table.setText(3, 0, "Multiple Values:");

        if (property.getId() != null) {
            table.setText(3, 1, property.isMultiValued() ? "True" : "False");
        } else {
            multivalue = new CheckBox();
            multivalue.setChecked(property.isMultiValued());
            table.setWidget(3, 1, multivalue);
        }
    }

    protected void fetchItem(String id) {
        adminPanel.getRegistryService().getPropertyDescriptor(id, getFetchCallback());
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

        if (!validate()) {
            return;
        }

        RegistryServiceAsync svc = adminPanel.getRegistryService();

        property.setDescription(descriptionTB.getText());
        property.setName(nameTB.getTextBox().getText());
        
        if (typeLB != null) {
            int idx = typeLB.getSelectedIndex();
            if (idx == 0) {
                property.setExtension(null);
            } else {
                property.setExtension(typeLB.getValue(idx));
            }
        }
        
        if (multivalue != null) {
            property.setMultiValued(multivalue.isChecked());
        }
        
        HashMap<String, String> config = new HashMap<String, String>();
        property.setConfiguration(config);
        for (Map.Entry<String, ValidatableTextBox> e : fields.entrySet()) {
            
            ValidatableTextBox tb = e.getValue();
            
            config.put(e.getKey(), tb.getTextBox().getText());
        }
        
         svc.savePropertyDescriptor(property, getSaveCallback());
    }

    protected void delete() {
        final ConfirmDialog dialog = new ConfirmDialog(new ConfirmDialogAdapter() {
            public void onConfirm() {
                PropertyDescriptorForm.super.delete();
                RegistryServiceAsync svc = adminPanel.getRegistryService();
                svc.deletePropertyDescriptor(property.getId(), getDeleteCallback());
            }
        }, "Are you sure you want to delete property " + property.getName() + "?");
        new LightBox(dialog).show();
    }

    protected boolean validate() {
        boolean isOk = true;

        isOk &= nameTB.validate();
        
        for (Iterator<ValidatableTextBox> itr = fields.values().iterator(); itr.hasNext();) {
            ValidatableTextBox tb = itr.next();
            
            isOk &= tb.validate();
        }
        return isOk;
    }

}
