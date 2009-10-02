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

import org.mule.galaxy.web.rpc.WRole;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.FlexTable;


public class RoleForm extends AbstractAdministrationForm {

    private WRole role;
    private TextField<String> nameTB;

    public RoleForm(AdministrationPanel adminPanel) {
        super(adminPanel, "roles", "Role was saved.", "Role was deleted.",
                "A role with that name already exists.");
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");

        nameTB = new TextField<String>();
        nameTB.setAllowBlank(false);
        nameTB.setValue(role.getName());

        table.setWidget(0, 1, nameTB);

        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        getSecurityService().getGroup(id, getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return "Add Role";
        } else {
            return "Edit Role: " + role.getName();
        }
    }

    protected void initializeItem(Object o) {
        role = (WRole) o;
    }

    protected void initializeNewItem() {
        role = new WRole();
    }

    protected void save() {

        if(!nameTB.validate()) {
            return;
        }                       


        super.save();
        role.setName(nameTB.getValue());
        getSecurityService().save(role, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
          public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();

            if (Dialog.YES.equals(btn.getItemId())) {
                RoleForm.super.delete();
                getSecurityService().deleteGroup(role.getId(), getDeleteCallback());
            }
          }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete role " + role.getName() + "?", l);
    }

}
