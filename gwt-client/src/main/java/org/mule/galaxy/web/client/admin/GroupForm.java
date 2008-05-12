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

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;

public class GroupForm extends AbstractAdministrationForm {

    private WGroup group;
    private TextBox nameTB;
    
    public GroupForm(AdministrationPanel adminPanel) {
        super(adminPanel, "groups", "Group was saved.", "Group was deleted.");
    }

    protected void addFields(FlexTable table) {
        table.setText(0, 0, "Name:");
        
        nameTB = new TextBox();
        nameTB.setText(group.getName());
        table.setWidget(0, 1, nameTB);
        
        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        getSecurityService().getGroup(group.getId(), getFetchCallback());
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
        super.save();
        
        group.setName(nameTB.getText());
        getSecurityService().save(group, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        getSecurityService().deleteGroup(group.getId(), getDeleteCallback());
    }
}
