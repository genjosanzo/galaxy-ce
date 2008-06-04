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

import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.validation.StringNotEmptyValidator;
import org.mule.galaxy.web.client.validation.ui.ValidatableTextBox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

import org.gwtwidgets.client.ui.LightBox;

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private ValidatableTextBox nameTB;
    private TextBox descriptionTB;

    public PropertyDescriptorForm(AdministrationPanel adminPanel){
        super(adminPanel, "properties", "Property was saved.", "Property was deleted.", 
              "A property with that name already exists.");
    }
    
    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Description:");
//        table.setText(2, 0, "Multivalued");
        
        nameTB = new ValidatableTextBox(new StringNotEmptyValidator());
        nameTB.getTextBox().setText(property.getName());

        table.setWidget(0, 1, nameTB);
        
        descriptionTB = new TextBox();
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);

        styleHeaderColumn(table);
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

        return isOk;
    }

}
