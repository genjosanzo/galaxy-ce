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

import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_CHECKBOX_PASSWORD_RESET_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_EMAIL_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_GROUPS_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_NAME_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_NEW_PASSWORD2_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_NEW_PASSWORD_BUTTON_CANCEL_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_NEW_PASSWORD_BUTTON_OK_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_NEW_PASSWORD_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_PASSWORD2_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_PASSWORD_ID;
import static org.mule.galaxy.web.client.ClientId.MANAGE_USER_USERNAME_ID;

import java.util.Collection;

import org.mule.galaxy.web.client.ui.AbstractErrorHandlingPopup;
import org.mule.galaxy.web.client.ui.dialog.LightBox;
import org.mule.galaxy.web.client.ui.help.AdministrationConstants;
import org.mule.galaxy.web.client.ui.help.AdministrationMessages;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.client.ui.panel.SelectionPanel;
import org.mule.galaxy.web.client.ui.panel.SelectionPanel.ItemInfo;
import org.mule.galaxy.web.client.ui.validator.EmailValidator;
import org.mule.galaxy.web.client.ui.validator.RegexValidator;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WUser;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class UserForm extends AbstractAdministrationForm {

    private static final int PASSWORD_MIN_LENGTH = 5;
    
    private static final String UBER_USER = "admin";
    private static final String UBER_GROUP = "Administrators";
    
    private WUser user;
    private TextField<String> passTB;
    private TextField<String> confirmTB;
    private TextField<String> nameTB;
    private TextField<String> emailTB;
    private TextField<String> usernameTB;
    private SelectionPanel groupPanel;
    private CheckBox resetPassword;
    
    private Collection<WGroup> wgroups;
    private static final AdministrationConstants administrationConstants = (AdministrationConstants) GWT.create(AdministrationConstants.class);
    private static final AdministrationMessages administrationMessages = (AdministrationMessages) GWT.create(AdministrationMessages.class);

    public UserForm(AdministrationPanel adminPanel) {
        super(adminPanel, "users", administrationConstants.userSaved(), administrationConstants.userDeleted(),
                administrationConstants.userExists());

       setHelpPanel(new InlineHelpPanel(administrationConstants.addUserTip(), 21));
    }

    protected void addFields(final FlexTable table) {
        // a simple row counter to simplify table.setWidget() calls
        int row = 0;
        table.setText(row++, 0, administrationConstants.usernameForm());
        table.setText(row++, 0, administrationConstants.name());
        table.setText(row++, 0, administrationConstants.email());
        if (newItem) {
            table.setText(row++, 0, administrationConstants.password());
            table.setText(row++, 0, administrationConstants.confirmPassword());
        } else {
            table.setText(row++, 0, "");
        }
        table.setText(row++, 0, administrationConstants.groups());

        // reset row counter for input fields
        row = 0;

        if (newItem) {
            usernameTB = new TextField<String>();
            usernameTB.setId(MANAGE_USER_USERNAME_ID);
            usernameTB.setValidateOnBlur(true);
            usernameTB.setAllowBlank(false);
            usernameTB.setValidator(new RegexValidator(".*/.*") {
                @Override
                public String validate(Field<?> field, String s) {
                    final String reason = super.validate(field, s);
                    if (reason != null) {
                        return administrationConstants.cannotContain();
                    } else if ((s == null || s.trim().length() < 1)) {
                        return administrationConstants.fieldRequired();
                    }
                    return null;
                }
            });
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
        nameTB.setId(MANAGE_USER_NAME_ID);
        nameTB.setAllowBlank(false);
        nameTB.setValue(user.getName());
        table.setWidget(row, 1, nameTB);
        /*
         * Add an extender in the table to align the validation label 
         * otherwise groups cell stretches and deforms the cell
         */
        table.setWidget(row, 2, new Label(" "));
        table.getCellFormatter().setWidth(row, 2, "100%");

        row++;
        emailTB = new TextField<String>();
        emailTB.setId(MANAGE_USER_EMAIL_ID);
        emailTB.setAllowBlank(false);
        emailTB.setValue(user.getEmail());
        emailTB.setValidator(new EmailValidator());
        table.setWidget(row, 1, emailTB);

        if (newItem) {
            row++;
            passTB = createPasswordTextBox(MANAGE_USER_PASSWORD_ID);
            table.setWidget(row, 1, passTB);

            row++;
            confirmTB = createPasswordConfirmTextBox(MANAGE_USER_PASSWORD2_ID);
            table.setWidget(row, 1, confirmTB);
        } else {
            row++;
            resetPassword = new CheckBox(administrationConstants.resetPassword());
            resetPassword.getElement().setId(MANAGE_USER_CHECKBOX_PASSWORD_RESET_ID);
            table.setWidget(row, 1, resetPassword);
            resetPassword.addClickListener(new ClickListener() {
                public void onClick(final Widget widget) {
                    // It's critical to use the passed in event source, otherwise the event fires 3 times (?!)
                    if (((CheckBox) widget).getValue()) {
                        // only show the dialog if enabling the checkbox
                        new LightBox(new ResetPasswordDialog()).show();
                    }
                }
            });
        }

        row++;
        table.setText(row, 1, administrationConstants.loadingGroups());
        // temp var for anonymous class
        final int groupRow = row;
        getSecurityService().getGroups(new AbstractCallback(adminPanel) {
            @Override
            public void onCallSuccess(Object groups) {
                receiveGroups((Collection<WGroup>) groups, table, groupRow);
            }
        });

        styleHeaderColumn(table);
    }

    private TextField<String> createPasswordConfirmTextBox(String id) {
        TextField<String> confirmTB = new TextField<String>();
        if(id != null) {
            confirmTB.setId(id); 
        }
        confirmTB.setAllowBlank(false);
        confirmTB.setPassword(true);
        confirmTB.setMinLength(PASSWORD_MIN_LENGTH);
        confirmTB.setToolTip(administrationMessages.charactersLength(PASSWORD_MIN_LENGTH));
        return confirmTB;
    }

    private TextField<String> createPasswordTextBox(String id) {
        TextField<String> passTB = new TextField<String>();
        if(id != null) {
            passTB.setId(id); 
        }
        passTB.setAllowBlank(false);
        passTB.setPassword(true);
        passTB.setToolTip(administrationMessages.charactersLength(PASSWORD_MIN_LENGTH));
        passTB.setMinLength(PASSWORD_MIN_LENGTH);
        return passTB;
    }

    protected void fetchItem(String id) {
        getSecurityService().getUser(id, getFetchCallback());
    }

    public String getTitle() {
        if (newItem) {
            return administrationConstants.addUser();
        } else {
            return administrationConstants.editUser() + user.getUsername();
        }
    }

    protected void initializeItem(Object o) {
        this.user = (WUser) o;
    }

    protected void initializeNewItem() {
        this.user = new WUser();
    }

    protected void receiveGroups(Collection<WGroup> groups, FlexTable table, final int currentRow) {
        this.wgroups = groups;
        ItemInfo itemInfo = new ItemInfo() {
            public String getText(Object o) {
                return ((WGroup) o).getName();
            }

            public String getValue(Object o) {
                return ((WGroup) o).getId();
            }
        };
        groupPanel = new SelectionPanel(groups, itemInfo, user.getGroupIds(), 6, administrationConstants.availableGroups(), administrationConstants.joinedGroups());
        groupPanel.getElement().setId(MANAGE_USER_GROUPS_ID);
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

        String password = passTB != null ? passTB.getValue() : null;
        String confirmationPassword = confirmTB != null ? confirmTB.getValue() : null;

        SecurityServiceAsync svc = getSecurityService();
        if (newItem) {
            save(svc, password, confirmationPassword);
        } else {
            update(svc, password, confirmationPassword);
        }
    }

    public boolean validate() {
        getErrorPanel().clearErrorMessage();
        boolean isOk = true;

        // Username textbox is not there on Edit screen, only Add.
        if (newItem) {
            isOk &= usernameTB.validate();
        }
        isOk &= nameTB.validate();
        isOk &= emailTB.validate();
        if (newItem || (resetPassword != null && resetPassword.getValue())) {
            isOk &= passTB.validate();
            isOk &= confirmTB.validate();
            // Passwords must match.
            if (!passTB.getValue().equals(confirmTB.getValue())) {
                getErrorPanel().addMessage(administrationConstants.passwordsMatch());
                isOk = false;
            }
        }

        // At least one group must be selected.
        if (groupPanel.getSelectedValues().isEmpty()) {
            getErrorPanel().addMessage(administrationConstants.userWarning());
            isOk = false;
        }

        // Make sure admin user is still a member of the Administrators group.
        if (!(newItem) && user.getUsername().equals(UBER_USER)) {
            if (!(groupPanel.getSelectedValues().contains(getAdminGroupKey(this.wgroups)))) {
                getErrorPanel().addMessage(UBER_USER + administrationMessages.userMemberOf(UBER_GROUP));
                isOk = false;
            }
        }
        return isOk;
    }

    /* find the key that maps to the Administrator group and verify */
    private String getAdminGroupKey(Collection<WGroup> groups) {
        for( WGroup wg : groups) {
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
        MessageBox.confirm(administrationConstants.confirm(), administrationConstants.deleteUser() + user.getName() + " (" + user.getUsername() + ")?", l);
    }

    private class ResetPasswordDialog extends AbstractErrorHandlingPopup {

        public ResetPasswordDialog() {

            fpanel.setHeaderVisible(true);
            fpanel.setHeading(administrationConstants.resetPasswordButton());

            passTB = createPasswordTextBox(MANAGE_USER_NEW_PASSWORD_ID);
            passTB.setFieldLabel(administrationConstants.newPassword());

            confirmTB = createPasswordConfirmTextBox(MANAGE_USER_NEW_PASSWORD2_ID);
            confirmTB.setFieldLabel(administrationConstants.confirmPasswordField());

            fpanel.add(passTB);
            fpanel.add(confirmTB);

            Button okButton = new Button("OK");
            okButton.setId(MANAGE_USER_NEW_PASSWORD_BUTTON_OK_ID);
            okButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    boolean isOk = passTB.validate();
                    isOk &= confirmTB.validate();
                    if (isOk) {
                        ResetPasswordDialog.this.hide();
                    }
                }
            });

            Button cancelButton = new Button(administrationConstants.cancel());
            cancelButton.setId(MANAGE_USER_NEW_PASSWORD_BUTTON_CANCEL_ID);
            cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                @Override
                public void componentSelected(ButtonEvent ce) {
                    ResetPasswordDialog.this.hide();
                    passTB.setValue(null);
                    confirmTB.setValue(null);
                    resetPassword.setValue(false);
                }
            });

            ButtonBar bb = new ButtonBar();
            bb.add(okButton);
            bb.add(cancelButton);

            fpanel.add(bb);
        }
    }

    public TextField<String> getUsernameField() {
        return usernameTB;
    }

    public TextField<String> getNameField() {
        return nameTB;
    }

    public TextField<String> getEmailField() {
        return emailTB;
    }
}
