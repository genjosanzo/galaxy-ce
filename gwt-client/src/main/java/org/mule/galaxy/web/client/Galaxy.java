package org.mule.galaxy.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.activity.ActivityPanel;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.artifact.ArtifactPanel;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.UserServiceAsync;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy implements EntryPoint, HistoryListener {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private UserServiceAsync userService;
    
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

        SimplePanel header = new SimplePanel();
        header.setStyleName("header");
        header.add(new Image("images/galaxy_small_logo.png"));

        base.add(header);
        
        TabPanel tabPanel = new TabPanel();
        base.add(tabPanel);
        
        registryPanel = new RegistryPanel(this);
        tabPanel.add(registryPanel, "Registry");
        tabPanel.setStyleName("headerTabPanel");
        tabPanel.getDeckPanel().setStyleName("headerTabDeckPanel");
        tabPanel.selectTab(0);
        tabPanel.add(new ActivityPanel(this), "Activity");
        tabPanel.add(new AdministrationPanel(this), "Administration");
        
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
