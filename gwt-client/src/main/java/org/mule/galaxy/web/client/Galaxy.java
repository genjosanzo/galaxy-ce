package org.mule.galaxy.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.artifact.ArtifactPanel;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy implements EntryPoint, HistoryListener {

    RegistryServiceAsync service;
    private RegistryPanel registryPanel;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        History.addHistoryListener(this);
        
        FlowPanel base = new FlowPanel();
        base.setStyleName("base");
        base.setWidth("100%");
        
        Label header = new Label("Mule Galaxy");
        header.setStyleName("header");
        base.add(header);
        
        TabPanel tabPanel = new TabPanel();
        base.add(tabPanel);
        
        registryPanel = new RegistryPanel(this);
        tabPanel.add(registryPanel, "Registry");
        tabPanel.selectTab(0);
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
        
        if (token.startsWith("artifact-")) {
            registryPanel.setMessage("Found token");
            registryPanel.setMain(new ArtifactPanel(registryPanel, token.substring(9)));
        }
    }

}
