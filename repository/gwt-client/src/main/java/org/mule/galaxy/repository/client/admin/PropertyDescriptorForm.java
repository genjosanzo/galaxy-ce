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

package org.mule.galaxy.repository.client.admin;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.rpc.WPropertyDescriptor;
import org.mule.galaxy.web.client.admin.AbstractAdministrationForm;
import org.mule.galaxy.web.client.admin.AdministrationPanel;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.ui.FlexTable;

public class PropertyDescriptorForm extends AbstractAdministrationForm {

    private WPropertyDescriptor property;
    private InnerPropertyDescriptorForm innerForm;
    private final RepositoryModule repositoryModule;

    public PropertyDescriptorForm(AdministrationPanel adminPanel, 
                                  RepositoryModule repositoryModule){
        super(adminPanel, "properties", "Property was saved.", "Property was deleted.",
              "A property with that name already exists.");
        this.repositoryModule = repositoryModule;
    }

    protected void addFields(final FlexTable table) {
        this.innerForm = new InnerPropertyDescriptorForm();
        innerForm.initialize(galaxy, repositoryModule.getPropertyInterfaceManager(), property, table);
    }

    protected void fetchItem(String id) {
        repositoryModule.getRegistryService().getPropertyDescriptor(id, getFetchCallback());
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

        repositoryModule.getRegistryService().savePropertyDescriptor(property, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
          public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();

            if (Dialog.YES.equals(btn.getItemId())) {
              PropertyDescriptorForm.super.delete();
              repositoryModule.getRegistryService().deletePropertyDescriptor(property.getId(), getDeleteCallback());
            }
          }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete property " + property.getName() + "?", l);
    }

    protected boolean validate() {
        return innerForm.validate();
    }

}
