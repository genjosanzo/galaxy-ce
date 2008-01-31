package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.UserServiceAsync;
import org.mule.galaxy.web.rpc.WUser;

public class UserForm extends AbstractComposite {

    private AdministrationPanel adminPanel;
    private WUser user;
    private PasswordTextBox passTB;
    private PasswordTextBox confirmTB;
    private Button save;
    private TextBox nameTB;
    private TextBox emailTB;
    private final boolean add;
    private TextBox usernameTB;
    private CheckBox adminCB;

    public UserForm(AdministrationPanel adminPanel, WUser u) {
        this (adminPanel, u, false);
    }
    
    public UserForm(AdministrationPanel adminPanel) {
        this (adminPanel, new WUser(), true);
    }
    
    protected UserForm(AdministrationPanel adminPanel, WUser u, boolean add){
        this.adminPanel = adminPanel;
        this.user = u;
        this.add = add;
        
        FlowPanel panel = new FlowPanel();
        String title;
        if (add) {
            title = "Add UserImpl";
        } else {
            title = "Edit UserImpl " + u.getUsername();
        }
        
        final FlexTable table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Username");
        table.setText(1, 0, "Name");
        table.setText(2, 0, "Email");
        table.setText(3, 0, "Password");
        table.setText(4, 0, "Confirm Password");
        table.setText(5, 0, "Administrator");
        
        if (add) {
            usernameTB = new TextBox();
            table.setWidget(0, 1, usernameTB);
        } else {
            table.setText(0, 1, u.getUsername());
        }
        
        nameTB = new TextBox();
        nameTB.setText(u.getName());
        table.setWidget(1, 1, nameTB);
        
        emailTB = new TextBox();
        emailTB.setText(u.getEmail());
        table.setWidget(2, 1, emailTB);

        passTB = new PasswordTextBox();
        table.setWidget(3, 1, passTB);
        
        confirmTB = new PasswordTextBox();
        table.setWidget(4, 1, confirmTB);

        adminCB = new CheckBox();
        adminCB.setChecked(u.isAdmin());
        if ("admin".equals(u.getUsername())) {
            adminCB.setEnabled(false);
        }
        
        table.setWidget(5, 1, adminCB);
        
        save = new Button("Save");
        table.setWidget(6, 1, save);
        save.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                save();
            }
            
        });
        
        styleHeaderColumn(table);
        initWidget(panel);
    }

    protected void save() {
        save.setEnabled(false);
        save.setText("Saving...");
        
        UserServiceAsync svc = adminPanel.getUserService();
        
        String p = passTB.getText();
        String c = confirmTB.getText();
        
        if (p != null && !p.equals(c)){
            adminPanel.setMessage("The confirmation password does not match.");
            reenable();
            return;
        }
    
        if (usernameTB != null) {
            user.setUsername(usernameTB.getText());
        }
        
        user.setEmail(emailTB.getText());
        user.setName(nameTB.getText());
        user.setAdmin(adminCB.isChecked());
        
        if (add) {
            save(svc, p, c);
        } else {
            update(svc, p, c);
        }
    }

    private void update(UserServiceAsync svc, String p, String c) {
        svc.updateUser(user, p, c, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("UserImpl was not found! " + user.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("UserImpl " + user.getUsername() + " was saved.");
            }
            
        });
    }

    private void save(UserServiceAsync svc, String p, String c) {
        svc.addUser(user, p, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("UserImpl was not found! " + user.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("UserImpl " + user.getUsername() + " was created.");
            }
            
        });
    }
    
    private void reenable() {
        save.setEnabled(true);
        save.setText("Save");
    }

}
