package org.mule.galaxy.web.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractMenuPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.UserServiceAsync;

public class AdministrationPanel extends AbstractMenuPanel {

    private UserServiceAsync userService;
    private Hyperlink add;
    private RegistryServiceAsync registryService;
    
    public AdministrationPanel(Galaxy galaxy) {
        super(galaxy);
        
        userService = (UserServiceAsync) GWT.create(UserService.class);
        this.registryService = galaxy.getRegistryService();
        
        Toolbox manageBox = new Toolbox();
        manageBox.setTitle("Manage");
        addMenuItem(manageBox);
        
        final AdministrationPanel adminPanel = this;
        
        ServiceDefTarget target = (ServiceDefTarget) userService;
        target.setServiceEntryPoint("/handler/userService.rpc");
        
        Hyperlink link = new Hyperlink("Lifecycles", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUsers();
            }
        });
        manageBox.add(link);
        
        link = new Hyperlink("Policies", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new PolicyPanel(adminPanel));
            }
        });
        manageBox.add(link);
        
        link = new Hyperlink("Queries", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUsers();
            }
        });
        manageBox.add(link);
        
        link = new Hyperlink("Users", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUsers();
            }
        });
        
        add = new Hyperlink("Add","");
        add.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new UserForm(adminPanel));
            }
        });
        HorizontalPanel item = new HorizontalPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));
        
        manageBox.add(item);
    }


    protected void addUser() {
        // TODO Auto-generated method stub
        
    }


    public void showUsers() {
        setMain(new UserListPanel(this));
    }

    public UserServiceAsync getUserService() {
        return userService;
    }


    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }
}
