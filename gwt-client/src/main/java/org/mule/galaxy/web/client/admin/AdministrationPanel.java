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

    private DockPanel adminPanel;
    private UserServiceAsync userService;
    
    public AdministrationPanel() {
        super();
        
        userService = (UserServiceAsync) GWT.create(UserService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) userService;
        target.setServiceEntryPoint("/handler/userService.rpc");
        
        Hyperlink usersLink = new Hyperlink("Users", "");
        usersLink.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUserPanel();
            }
        });
        
        addMenuItem(usersLink);
    }

    protected void showUserPanel() {
        setMain(new UserListPanel(userService));
    }

}
