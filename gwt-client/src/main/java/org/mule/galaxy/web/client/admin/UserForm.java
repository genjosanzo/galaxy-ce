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

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.util.SelectionPanel;
import org.mule.galaxy.web.client.util.SelectionPanel.ItemInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WRole;
import org.mule.galaxy.web.rpc.WUser;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

public class UserForm extends AbstractAdministrationForm {

    private WUser user;
    private TextField<String> passTB;
    private TextField<String> confirmTB;
    private TextField<String> nameTB;
    private TextField<String> emailTB;
    private TextField<String> usernameTB;
    private SelectionPanel groupPanel;
    private CheckBox resetPassword;
    private static final int PASSWORD_MIN_LENGTH = 5;
    private Collection wgroups;
    private static final String UBER_USER = "admin";
    private static final String UBER_GROUP = "Administrators";

    public UserForm(AdministrationPanel adminPanel) {
        super(adminPanel, "users", "User was saved.", "User was deleted.",
                "A user with that username already exists.");
    }

    protected void addFields(final FlexTable table) {
        // a simple row counter to simplify table.setWidget() calls
        int row = 0;
        table.setText(row++, 0, "Username:");
        table.setText(row++, 0, "Name:");
        table.setText(row++, 0, "Email:");
        if (newItem) {
            table.setText(row++, 0, "Password:");
            table.setText(row++, 0, "Confirm Password:");
        } else {
            table.setText(row++, 0, "");
        }
        table.setText(row++, 0, "Groups:");

        // reset row counter for input fields
        row = 0;

        if (newItem) {
            usernameTB = new TextField<String>();
            usernameTB.setAllowBlank(false);
            table.setWidget(row, 1, usernameTB);
        } else {
            table.setText(row, 1, user.getUsername());
            // admin user is not to be deleted
            // would be nice to remove the button maybe?
            if (user.getUsername().equals(UBER_USER)) {
                getDelete().setEnabled(false);
            }
        }

        row++;
        nameTB = new TextField<String>();
        nameTB.setAllowBlank(false);
        nameTB.setValue(user.getName());
        table.setWidget(row, 1, nameTB);
        // add an extender in the table to align the validation label
        // otherwise groups cell stretches and deforms the cell
        table.setWidget(row, 2, new Label(" "));
        table.getCellFormatter().setWidth(row, 2, "100%");

        row++;
        emailTB = new TextField<String>();
        emailTB.setAllowBlank(false);
        emailTB.setValue(user.getEmail());
        table.setWidget(row, 1, emailTB);

        if (newItem) {
            row++;
            //passTB = new ValidatablePasswordTextBox(new MinLengthValidator(PASSWORD_MIN_LENGTH));
            passTB = new TextField<String>();
            passTB.setAllowBlank(false);
            passTB.setPassword(true);
            passTB.setToolTip("Must be at least " + PASSWORD_MIN_LENGTH + " characters in length");
            passTB.setMinLength(PASSWORD_MIN_LENGTH);
            table.setWidget(row, 1, passTB);

            row++;
            //confirmTB = new ValidatablePasswordTextBox(new MinLengthValidator(PASSWORD_MIN_LENGTH));
            confirmTB = new TextField<String>();
            confirmTB.setAllowBlank(false);
            confirmTB.setPassword(true);
            confirmTB.setMinLength(PASSWORD_MIN_LENGTH);
            confirmTB.setToolTip("Must be at least " + PASSWORD_MIN_LENGTH + " characters in length");
            table.setWidget(row, 1, confirmTB);
        } else {
            row++;
            resetPassword = new CheckBox("Reset Password");
            table.setWidget(row, 1, resetPassword);
            resetPassword.addClickListener(new ClickListener() {
                public void onClick(final Widget widget) {
                    // It's critical to use the passed in event source, otherwise the event fires 3 times (?!)
                    if (((CheckBox) widget).isChecked()) {
                        // only show the dialog if enabling the checkbox
                        new LightBox(new ResetPasswordDialog()).show();
                    }
                }
            });
        }

        row++;
        table.setText(row, 1, "Loading Groups...");
        // temp var for anonymous class
        final int groupRow = row;
        getSecurityService().getGroups(new AbstractCallback(adminPanel) {
            public void onSuccess(Object groups) {
                receiveGroups((Collection) groups, table, groupRow);
            }
        });

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

    protected void receiveGroups(Collection groups, FlexTable table, final int currentRow) {
        this.wgroups = groups;
        ItemInfo itemInfo = new ItemInfo() {
            public String getText(Object o) {
                return ((WRole) o).getName();
            }

            public String getValue(Object o) {
                return ((WRole) o).getId();
            }
        };
        groupPanel = new SelectionPanel(groups, itemInfo,
                user.getGroupIds(), 6,
                "Available Groups", "Joined Groups");
        table.setWidget(currentRow, 1, groupPanel);
        FlexTable.FlexCellFormatter cellFormatter = (FlexTable.FlexCellFormatter) table.getCellFormatter();
        cellFormatter.setColSpan(currentRow, 1, 2);
    }

    protected void save() {
        if (!validate()) {
            return;
        }

        super.save();

        if (usernameTB != null) {
            user.setUsername(usernameTB.getValue());
        }

        user.setEmail(emailTB.getValue());
        user.setName(nameTB.getValue());
        user.setGroupIds(groupPanel.getSelectedValues());

        String p = passTB != null ? passTB.getValue() : null;
        String c = confirmTB != null ? confirmTB.getValue() : null;

        SecurityServiceAsync svc = getSecurityService();
        if (newItem) {
            save(svc, p, c);
        } else {
            update(svc, p, c);
        }
    }

    protected boolean validate() {
        getErrorPanel().clearErrorMessage();
        boolean isOk = true;

        // username textbox is not there on Edit screen, only Add
        if (newItem) {
            isOk &= usernameTB.validate();
        }
        isOk &= nameTB.validate();
        isOk &= emailTB.validate();
        if (newItem || (resetPassword != null && resetPassword.isChecked())) {
            isOk &= passTB.validate();
            isOk &= confirmTB.validate();
            // passwords must match
            if (!passTB.getValue().equals(confirmTB.getValue())) {
                getErrorPanel().addMessage("Passwords must match");
                isOk = false;
            }
        }

        // at least one group must be selected
        if (groupPanel.getSelectedValues().isEmpty()) {
            getErrorPanel().addMessage("User must be a member of at least one group");
            isOk = false;
        }

        // make sure admin user is still a member of the Administrators group
        if (!(newItem) && user.getUsername().equals(UBER_USER)) {
            if (!(groupPanel.getSelectedValues().contains(getAdminGroupKey(this.wgroups)))) {
                getErrorPanel().addMessage(UBER_USER + " user must be a member of the " + UBER_GROUP + " group");
                isOk = false;
            }
        }

        return isOk;
    }


    /* find the key that maps to the Administrator group and verify */
    private String getAdminGroupKey(Collection groups) {
        for (Iterator itr = groups.iterator(); itr.hasNext();) {
            WRole wg = (WRole) itr.next();
            if (wg.getName().equals(UBER_GROUP)) {
                return wg.getId();
            }
        }
        return null;
    }


    private void update(SecurityServiceAsync svc, String p, String c) {
        svc.updateUser(user, p, c, getSaveCallback());
    }

    private void save(SecurityServiceAsync svc, String p, String c) {
        svc.addUser(user, p, getSaveCallback());
    }

    protected void delete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    UserForm.super.delete();
                    getSecurityService().deleteUser(user.getId(), getDeleteCallback());
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete user " + user.getName() + " (" + user.getUsername() + ")?", l);
    }

    private class ResetPasswordDialog extends DialogBox {

        public ResetPasswordDialog() {
            setText("Reset Password");

            VerticalPanel main = new VerticalPanel();
            //passTB = new ValidatablePasswordTextBox(new MinLengthValidator(PASSWORD_MIN_LENGTH));
            //confirmTB = new ValidatablePasswordTextBox(new MinLengthValidator(PASSWORD_MIN_LENGTH));
            InlineFlowPanel row = new InlineFlowPanel();
            Label label = new Label("New Password:");
            label.addStyleName("form-label");
            row.add(label);
            row.add(passTB);
            main.add(row);

            row = new InlineFlowPanel();
            label = new Label("Confirm Password:");
            label.addStyleName("form-label");
            row.add(label);
            row.add(confirmTB);
            main.add(row);

            row = new InlineFlowPanel();
            row.addStyleName("buttonRow");
            Button okButton = new Button("OK");
            okButton.addClickListener(new ClickListener() {
                public void onClick(final Widget widget) {
                    boolean isOk = passTB.validate();
                    isOk &= confirmTB.validate();
                    if (isOk) {
                        ResetPasswordDialog.this.hide();
                    }
                }
            });

            row.add(okButton);
            Button cancelButton = new Button("Cancel");
            cancelButton.addClickListener(new ClickListener() {
                public void onClick(final Widget widget) {
                    ResetPasswordDialog.this.hide();
                    passTB.setValue(null);
                    confirmTB.setValue(null);
                    resetPassword.setChecked(false);
                }
            });
            row.add(cancelButton);
            main.add(row);

            setWidget(main);
        }
    }

}
