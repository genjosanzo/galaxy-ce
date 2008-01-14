package org.mule.galaxy.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.activity.ActivityPanel;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.UserServiceAsync;
import org.mule.galaxy.web.rpc.WUser;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy implements EntryPoint, HistoryListener {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private UserServiceAsync userService;
    private FlowPanel rightPanel;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        History.addHistoryListener(this);
        
        this.registryService = (RegistryServiceAsync) GWT.create(RegistryService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) registryService;
        target.setServiceEntryPoint(GWT.getModuleBaseURL() + "../handler/registry.rpc");
        
        this.userService = (UserServiceAsync) GWT.create(UserService.class);
        
        target = (ServiceDefTarget) userService;
        target.setServiceEntryPoint(GWT.getModuleBaseURL() + "../handler/userService.rpc");
        
        FlowPanel base = new FlowPanel();
        base.setStyleName("base");
        base.setWidth("100%");

        registryPanel = new RegistryPanel(this);

        final TabPanel tabPanel = new TabPanel();
        
        rightPanel = new FlowPanel();
        rightPanel.setStyleName("header-right");
        
        final Galaxy galaxy = this;
        registryService.getUserInfo(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                WUser user = (WUser) o;
                rightPanel.add(new Label(user.getName()));
                
                HTML logout = new HTML("<a href=\"" + GWT.getHostPageBaseURL() + "j_logout\">Logout</a>");
                rightPanel.add(logout);
                
                if (user.isAdmin()) {
                    tabPanel.add(new AdministrationPanel(galaxy), "Administration");
                }
            }
        });
        
        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        header.add(rightPanel);
        header.add(new Image("images/galaxy_small_logo.png"));

        base.add(header);
        base.add(tabPanel);
        
        tabPanel.insert(registryPanel, "Registry", 0);
        tabPanel.setStyleName("headerTabPanel");
        tabPanel.getDeckPanel().setStyleName("headerTabDeckPanel");
        tabPanel.selectTab(0);
        final ActivityPanel activityPanel = new ActivityPanel(this);
        tabPanel.insert(activityPanel, "Activity", 1);
        
        tabPanel.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents arg0, int tab) {
                if (tab == 1) {
                    activityPanel.refresh();
                }
            }
            
        });
        Label footer = new Label("Mule Galaxy, Copyright 2008 MuleSource, Inc.");
        footer.setStyleName("footer");
        base.add(footer);
        RootPanel.get().add(base);
    }

    public void show(HistoryWidget w) {
        
    }

    public void onHistoryChanged(String token) {
        // registryPanel.setMessage(token);
        
//        if (token.startsWith("artifact-")) {
//            registryPanel.setMain(new ArtifactPanel(registryPanel, token.substring(9)));
//        }
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }

    public UserServiceAsync getUserService() {
        return userService;
    }
    
    

}
