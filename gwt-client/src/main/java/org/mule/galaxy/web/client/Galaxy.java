package org.mule.galaxy.web.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.admin.AdministrationPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy implements EntryPoint {

    RegistryServiceAsync service;
    
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        service = (RegistryServiceAsync) GWT.create(RegistryService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) service;
        target.setServiceEntryPoint("/handler/registry.rpc");
        
        VerticalPanel base = new VerticalPanel();
        base.setStyleName("base");
        base.setWidth("100%");
        
        Label header = new Label("Mule Galaxy");
        header.setStyleName("header");
        base.add(header);
        
        TabPanel tabPanel = new TabPanel();
        base.add(tabPanel);
        
        tabPanel.add(new RepositoryPanel(service), "Registry");
        tabPanel.selectTab(0);
        tabPanel.add(new AdministrationPanel(), "Administration");
        
        RootPanel.get().add(base);
    }

}
