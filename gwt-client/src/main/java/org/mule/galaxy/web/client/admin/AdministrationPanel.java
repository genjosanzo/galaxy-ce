package org.mule.galaxy.web.client.admin;

import org.mule.galaxy.web.client.AbstractMenuPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdministrationPanel extends AbstractMenuPanel {

    private UserServiceAsync userService;
    
    public AdministrationPanel() {
        super();
        
        userService = (UserServiceAsync) GWT.create(UserService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) userService;
        target.setServiceEntryPoint("/handler/userService.rpc");
        
        final AdministrationPanel adminPanel = this;
        
        
        Hyperlink link = new Hyperlink("Lifecycles", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new UserListPanel(adminPanel));
            }
        });
        addMenuItem(link);
        
        link = new Hyperlink("Policies", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new UserListPanel(adminPanel));
            }
        });
        addMenuItem(link);
        
        link = new Hyperlink("Queries", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new UserListPanel(adminPanel));
            }
        });
        addMenuItem(link);
        
        link = new Hyperlink("Users", "");
        link.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                setMain(new UserListPanel(adminPanel));
            }
        });
        
        addMenuItem(link);
        
        
    }

    public UserServiceAsync getUserService() {
        return userService;
    }
}
