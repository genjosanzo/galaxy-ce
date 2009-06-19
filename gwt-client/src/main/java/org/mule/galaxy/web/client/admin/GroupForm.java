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

import org.mule.galaxy.web.rpc.WGroup;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.FlexTable;


public class GroupForm extends AbstractAdministrationForm {

    private WGroup group;
    private TextField<String> nameTB;

    public GroupForm(AdministrationPanel adminPanel) {
        super(adminPanel, "groups", "Group was saved.", "Group was deleted.",
                "A group with that name already exists.");
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");

        nameTB = new TextField<String>();
        nameTB.setAllowBlank(false);
        nameTB.setValue(group.getName());

        table.setWidget(0, 1, nameTB);

        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        getSecurityService().getGroup(id, getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return "Add Group";
        } else {
            return "Edit Group: " + group.getName();
        }
    }

    protected void initializeItem(Object o) {
        group = (WGroup) o;
    }

    protected void initializeNewItem() {
        group = new WGroup();
    }

    protected void save() {

        if(!nameTB.validate()) {
            return;
        }                       


        super.save();
        group.setName(nameTB.getValue());
        getSecurityService().save(group, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
          public void handleEvent(MessageBoxEvent ce) {
            Button btn = ce.getButtonClicked();

            if (Dialog.YES.equals(btn.getItemId())) {
                GroupForm.super.delete();
                getSecurityService().deleteGroup(group.getId(), getDeleteCallback());
            }
          }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete group " + group.getName() + "?", l);
    }

}
