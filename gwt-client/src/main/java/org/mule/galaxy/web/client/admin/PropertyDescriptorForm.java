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

import org.mule.galaxy.web.client.validation.CallbackValidator;
import org.mule.galaxy.web.client.validation.DigitsOnlyValidator;
import org.mule.galaxy.web.client.validation.FieldValidationListener;
import org.mule.galaxy.web.client.validation.StringNotBlankValidator;
import org.mule.galaxy.web.client.validation.ValidationListener;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import java.util.HashMap;
import java.util.Map;

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private TextBox nameTB;
    private TextBox descriptionTB;

    private TextBox deleteMeLater;

    /**
     * A simple map of input field -> validation listener for UI updates. 
     */
    private Map/*<Widget, ValidationListener>*/ validationListeners = new HashMap();

    public PropertyDescriptorForm(AdministrationPanel adminPanel){
        super(adminPanel, "properties", "Property was saved.", "Property was deleted.");
    }
    
    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");
        table.setText(1, 0, "Description:");
        table.setText(2, 0, "Demo (digits only):");
//        table.setText(2, 0, "Multivalued");
        
        nameTB = new TextBox();
        nameTB.setText(property.getName());

        // This is the label containing this fields' validation message in case of an error
        final Label nameValidationLabel = new Label();
        // some layout, put it right under the textbox
        FlowPanel p = new FlowPanel();
        p.add(nameTB);
        p.add(nameValidationLabel);
        // map the name textbox to this validation UI callback
        validationListeners.put(nameTB, new FieldValidationListener(nameValidationLabel));

        table.setWidget(0, 1, p);
        
        descriptionTB = new TextBox();
        descriptionTB.setText(property.getDescription());
        table.setWidget(1, 1, descriptionTB);

        deleteMeLater = new TextBox();
        // This is the label containing this fields' validation message in case of an error
        final Label demoFieldValidationLabel = new Label();
        // some layout, put it right under the textbox
        p = new FlowPanel();
        p.add(deleteMeLater);
        p.add(demoFieldValidationLabel);
        // map the demo textbox to this validation UI callback
        validationListeners.put(deleteMeLater, new FieldValidationListener(demoFieldValidationLabel));

        table.setWidget(2, 1, p);

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
        property.setName(nameTB.getText());

        svc.savePropertyDescriptor(property, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        RegistryServiceAsync svc = adminPanel.getRegistryService();
        
        svc.deletePropertyDescriptor(property.getId(), getDeleteCallback());
    }

    protected boolean validate() {
        boolean isOk = true;

        // name textbox
        TextBox source = nameTB;
        ValidationListener vl = (ValidationListener) validationListeners.get(source);
        CallbackValidator cbVal = new CallbackValidator(new StringNotBlankValidator(), vl, source);
        isOk &= cbVal.validate(source.getText());

        // demo textbox
        source = deleteMeLater;
        vl = (ValidationListener) validationListeners.get(source);
        cbVal = new CallbackValidator(new DigitsOnlyValidator(), vl, source);
        isOk &= cbVal.validate(source.getText());

        return isOk;
    }

}
