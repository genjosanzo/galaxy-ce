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

import org.mule.galaxy.web.client.util.SelectionPanel;
import org.mule.galaxy.web.client.util.SelectionPanel.ItemInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WUser;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;

import java.util.Collection;

public class UserForm extends AbstractAdministrationForm {

    private WUser user;
    private PasswordTextBox passTB;
    private PasswordTextBox confirmTB;
    private TextBox nameTB;
    private TextBox emailTB;
    private TextBox usernameTB;
    private SelectionPanel groupPanel;

    public UserForm(AdministrationPanel adminPanel) {
        super(adminPanel, "users", "User was saved.", "User was deleted.");
    }
    
    protected void addFields(final FlexTable table) {
        table.setText(0, 0, "Username:");
        table.setText(1, 0, "Name:");
        table.setText(2, 0, "Email:");
        table.setText(3, 0, "Password:");
        table.setText(4, 0, "Confirm Password:");
        table.setText(5, 0, "Groups:");
        
        if (newItem) {
            usernameTB = new TextBox();
            table.setWidget(0, 1, usernameTB);
        } else {
            table.setText(0, 1, user.getUsername());
        }
        
        nameTB = new TextBox();
        nameTB.setText(user.getName());
        table.setWidget(1, 1, nameTB);
        
        emailTB = new TextBox();
        emailTB.setText(user.getEmail());
        table.setWidget(2, 1, emailTB);

        passTB = new PasswordTextBox();
        table.setWidget(3, 1, passTB);
        
        confirmTB = new PasswordTextBox();
        table.setWidget(4, 1, confirmTB);

        getSecurityService().getGroups(new AbstractCallback(adminPanel) {
            public void onSuccess(Object groups) {
                receiveGroups((Collection) groups, table);
            }
        });
        
        table.setText(5, 1, "Loading Groups...");

        styleHeaderColumn(table);
    }

    protected void fetchItem(String id) {
        getSecurityService().getUser(id, getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return "Add User";
        } else {
            return "Edit User " + user.getUsername();
        }
    }

    protected void initializeItem(Object o) {
        this.user = (WUser) o;
    }

    protected void initializeNewItem() {
        this.user = new WUser();
    }

    protected void receiveGroups(Collection groups, FlexTable table) {
        ItemInfo itemInfo = new ItemInfo() {
            public String getText(Object o) {
                return ((WGroup) o).getName();
            }

            public String getValue(Object o) {
                return ((WGroup) o).getId();
            }
        };
        groupPanel = new SelectionPanel(groups, itemInfo, 
                                        user.getGroupIds(), 6, 
                                        "Available Groups", "Joined Groups");
        table.setWidget(5, 1, groupPanel);
    }

    protected void save() {
        super.save();
        
        SecurityServiceAsync svc = getSecurityService();
        
        String p = passTB.getText();
        String c = confirmTB.getText();
        
        if (p != null && !p.equals(c)){
            adminPanel.setMessage("The confirmation password does not match.");
            setEnabled(true);
            return;
        }
    
        if (usernameTB != null) {
            user.setUsername(usernameTB.getText());
        }
        
        user.setEmail(emailTB.getText());
        user.setName(nameTB.getText());
        user.setGroupIds(groupPanel.getSelectedValues());
        
        if (newItem) {
            save(svc, p, c);
        } else {
            update(svc, p, c);
        }
    }


    private void update(SecurityServiceAsync svc, String p, String c) {
        svc.updateUser(user, p, c, getSaveCallback());
    }

    private void save(SecurityServiceAsync svc, String p, String c) {
        svc.addUser(user, p, getSaveCallback());
    }

    protected void delete() {
        super.delete();
        
        getSecurityService().deleteUser(user.getId(),getDeleteCallback());
    }

}
