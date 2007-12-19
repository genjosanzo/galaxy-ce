package org.mule.galaxy.web.client.admin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AdministrationPanel extends com.google.gwt.user.client.ui.Composite {

    
    private DockPanel adminPanel;
    private Widget currentPanel;
    private UserServiceAsync userService;
    
    public AdministrationPanel() {
        super();
        
        userService = (UserServiceAsync) GWT.create(UserService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) userService;
        target.setServiceEntryPoint("/handler/userService.rpc");
        
        adminPanel = new DockPanel();
        VerticalPanel leftMenu = new VerticalPanel();
        
        
        Hyperlink usersLink = new Hyperlink("Users", "");
        usersLink.addClickListener(new ClickListener() {
            public void onClick(Widget w) {
                showUserPanel();
            }
        });
        
        leftMenu.add(usersLink);
        leftMenu.setStyleName("left-menu");
        
        adminPanel.add(leftMenu, DockPanel.WEST);
        
        initWidget(adminPanel);
    }

    protected void showUserPanel() {
        if (currentPanel != null) {
            adminPanel.remove(currentPanel);
        }
        
        currentPanel = new UserListPanel(userService);
        adminPanel.add(currentPanel, DockPanel.WEST);
    }

}
