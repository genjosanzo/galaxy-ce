package org.mule.galaxy.web.client.admin;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.UserServiceAsync;
import org.mule.galaxy.web.rpc.WUser;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class UserPanel extends Composite {

    private AdministrationPanel adminPanel;
    private WUser user;
    private PasswordTextBox oldPassTB;
    private PasswordTextBox passTB;
    private PasswordTextBox confirmTB;
    private Button save;
    private TextBox nameTB;
    private TextBox emailTB;

    public UserPanel(AdministrationPanel adminPanel, 
                     WUser u) {
        this.adminPanel = adminPanel;
        this.user = u;
        
        adminPanel.setTitle("Edit User");
        
        final FlexTable table = new FlexTable();
        table.setStyleName("gwt-FlexTable");
        table.setCellSpacing(0);
        table.setCellPadding(0);
        table.setWidth("100%");
        
        table.setText(0, 0, "Username");
        table.setText(1, 0, "Name");
        table.setText(2, 0, "Email");
        table.setText(3, 0, "Old Password");
        table.setText(4, 0, "Password");
        table.setText(5, 0, "Confirm Password");
        
        table.setText(0, 1, u.getUsername());
        
        nameTB = new TextBox();
        nameTB.setText(u.getName());
        table.setWidget(1, 1, nameTB);
        
        emailTB = new TextBox();
        emailTB.setText(u.getEmail());
        table.setWidget(2, 1, emailTB);

        oldPassTB = new PasswordTextBox();
        table.setWidget(3, 1, oldPassTB);
        
        passTB = new PasswordTextBox();
        table.setWidget(4, 1, passTB);
        
        confirmTB = new PasswordTextBox();
        table.setWidget(5, 1, confirmTB);
        
        save = new Button("Save");
        table.setWidget(6, 1, save);
        save.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                save();
            }
            
        });
        
        table.getRowFormatter().setStyleName(0, "gwt-FlexTable-header");
                
        initWidget(table);
    }

    protected void save() {
        save.setEnabled(false);
        save.setText("Saving...");
        
        UserServiceAsync svc = adminPanel.getUserService();
        
        String old = oldPassTB.getText();
        String p = passTB.getText();
        String c = confirmTB.getText();
        
        if (old != null || old.equals("")) {
            if (p == null || "".equals(p)){
                adminPanel.setMessage("You must specify a new password.");
                reenable();
            }
            
            if (!p.equals(c)){
                adminPanel.setMessage("The confirmation password does not match.");
                reenable();
            }
        }
    
        user.setEmail(emailTB.getText());
        user.setName(nameTB.getText());
        
        svc.updateUser(user, old, p, c, new AbstractCallback(adminPanel) {

            
            public void onFailure(Throwable caught) {
                if (caught instanceof PasswordChangeException) {
                    adminPanel.setMessage("Old password was not correct.");
                    reenable();
                } else if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("User was not found! " + user.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("User " + user.getUsername() + " was saved.");
            }
            
        });
    }

    private void reenable() {
        save.setEnabled(true);
        save.setText("Save");
    }

}
