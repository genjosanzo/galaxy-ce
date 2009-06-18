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

import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.WPropertyDescriptor;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.FlexTable;

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private InnerPropertyDescriptorForm innerForm;

    public PropertyDescriptorForm(AdministrationPanel adminPanel){
        super(adminPanel, "properties", "Property was saved.", "Property was deleted.",
              "A property with that name already exists.");
    }

    protected void addFields(final FlexTable table) {
        this.innerForm = new InnerPropertyDescriptorForm();
        innerForm.initialize(galaxy, property, table);
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

        property = innerForm.getPropertyDescriptor();

        galaxy.getRegistryService().savePropertyDescriptor(property, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
          public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();

            if (Dialog.YES.equals(btn.getItemId())) {
              PropertyDescriptorForm.super.delete();
              RegistryServiceAsync svc = adminPanel.getRegistryService();
              svc.deletePropertyDescriptor(property.getId(), getDeleteCallback());
            }
          }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete property " + property.getName() + "?", l);
    }

    protected boolean validate() {
        return innerForm.validate();
    }

}
