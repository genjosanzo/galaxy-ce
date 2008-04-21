package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.util.DeleteDialog;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.SelectionPanel;
import org.mule.galaxy.web.client.util.DeleteDialog.DeleteListener;
import org.mule.galaxy.web.client.util.SelectionPanel.ItemInfo;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemNotFoundException;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WGroup;
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
    private FlowPanel panel;
    private Button delete;
    private SelectionPanel groupPanel;

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
        
        panel = new FlowPanel();
        initWidget(panel);
    }
    
    public void onShow() {
        panel.clear();
        
        String title;
        if (add) {
            title = "Add User";
        } else {
            title = "Edit User " + user.getUsername();
        }
        
        final FlexTable table = createTitledColumnTable(panel, title);
        
        table.setText(0, 0, "Username:");
        table.setText(1, 0, "Name:");
        table.setText(2, 0, "Email:");
        table.setText(3, 0, "Password:");
        table.setText(4, 0, "Confirm Password:");
        table.setText(5, 0, "Groups:");
        
        if (add) {
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

        adminPanel.getSecurityService().getGroups(new AbstractCallback(adminPanel) {
            public void onSuccess(Object groups) {
                receiveGroups((Collection) groups, table);
            }
        });
        
        table.setText(5, 1, "Loading Groups...");
        
        save = new Button("Save");
        save.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                save();
            }
            
        });
        
        if (add || user.getUsername().equals("admin")) {
            table.setWidget(6, 1, save);
        } else {
            InlineFlowPanel buttons = new InlineFlowPanel();
            buttons.add(save);
            
            final DeleteDialog popup = new DeleteDialog("user", new DeleteListener() {
                public void onYes() {
                    delete();
                }
            });
            
            delete = new Button("Delete");
            delete.addClickListener(new ClickListener() {

                public void onClick(Widget sender) {
                    popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                        public void setPosition(int offsetWidth, int offsetHeight) {
                            int left = (Window.getClientWidth() - offsetWidth) / 3;
                            int top = (Window.getClientHeight() - offsetHeight) / 3;
                            popup.setPopupPosition(left, top);
                        }
                    });
                }
                
            });
            buttons.add(delete);
            
            table.setWidget(6, 1, buttons);
            
        }
        
        styleHeaderColumn(table);
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
        save.setEnabled(false);
        save.setText("Saving...");
        if (delete != null) {
            delete.setEnabled(false);
        }
        
        SecurityServiceAsync svc = adminPanel.getSecurityService();
        
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
        user.setGroupIds(groupPanel.getSelectedValues());
        
        if (add) {
            save(svc, p, c);
        } else {
            update(svc, p, c);
        }
    }


    private void update(SecurityServiceAsync svc, String p, String c) {
        svc.updateUser(user, p, c, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
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

    private void save(SecurityServiceAsync svc, String p, String c) {
        svc.addUser(user, p, new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("User was not found! " + user.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("User " + user.getUsername() + " was created.");
            }
            
        });
    }

    protected void delete() {
        save.setEnabled(false);
        delete.setEnabled(false);
        delete.setText("Deleting...");
        
        SecurityServiceAsync svc = adminPanel.getSecurityService();
        
        svc.deleteUser(user.getId(), new AbstractCallback(adminPanel) {

            public void onFailure(Throwable caught) {
                if (caught instanceof ItemNotFoundException) {
                    adminPanel.setMessage("User was not found! " + user.getId());
                    reenable();
                } else {
                    super.onFailure(caught);
                }
            }

            public void onSuccess(Object result) {
                adminPanel.showUsers();
                adminPanel.setMessage("User " + user.getUsername() + " was deleted.");
            }
            
        });
    }
    
    private void reenable() {
        if (delete != null) {
            delete.setEnabled(true);
            delete.setText("Delete");
        }
        save.setEnabled(true);
        save.setText("Save");
    }
}
