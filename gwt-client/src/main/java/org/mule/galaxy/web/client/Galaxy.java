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
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

import java.util.HashMap;
import java.util.Map;

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
    private PageInfo curInfo;
    private Map history = new HashMap();
    private TabPanel tabPanel;
    protected int oldTab;
    
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

        tabPanel = new TabPanel();
        
        rightPanel = new FlowPanel();
        rightPanel.setStyleName("header-right");

        
        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        header.add(rightPanel);
        header.add(new Image("images/galaxy_small_logo.png"));

        base.add(header);
        base.add(tabPanel);
        
        tabPanel.insert(registryPanel, "Registry", 0);
        tabPanel.setStyleName("headerTabPanel");
        tabPanel.getDeckPanel().setStyleName("headerTabDeckPanel");
        
        final ActivityPanel activityPanel = new ActivityPanel(this);
        tabPanel.insert(activityPanel, "Activity", 1);
        
        tabPanel.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents event, int newTab) {
                if (oldTab != newTab) {
                    if (curInfo != null) {
                        history.put("tab-" + oldTab, curInfo);
                    }
                }
                return true;
            }

            public void onTabSelected(SourcesTabEvents arg0, int tab) {
                if (oldTab != tab) {
                    History.newItem("tab-" + tab);
                }
                oldTab = tab;
            }
            
        });
        
        createPageForTab(0);
        createPageForTab(1);
        
        final Galaxy galaxy = this;
        registryService.getUserInfo(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                WUser user = (WUser) o;
                rightPanel.add(new Label(user.getName()));
                
                HTML logout = new HTML("<a href=\"" + GWT.getHostPageBaseURL() + "j_logout\">Logout</a>");
                rightPanel.add(logout);
                
                if (user.isAdmin()) {
                    tabPanel.add(new AdministrationPanel(galaxy), "Administration");
                    createPageForTab(2);
                    showFirstPage();
                }
            }
        });
        
        Label footer = new Label("Mule Galaxy, Copyright 2008 MuleSource, Inc.");
        footer.setStyleName("footer");
        base.add(footer);
        RootPanel.get().add(base);
    }
    
    private void createPageForTab(int i) {
        final AbstractComposite ac = (AbstractComposite) tabPanel.getWidget(i);
        
        PageInfo page = new PageInfo("tab-" + i, i) {
            public AbstractComposite createInstance() {
                return ac;
            }

            public void show() {
            }
        };
        
        history.put(page.getName(), page);
    }

    protected void showFirstPage() {
        // Show the initial screen.
        String initToken = History.getToken();
        if (initToken.length() > 0) {
            onHistoryChanged(initToken);
        } else {
            tabPanel.selectTab(0);
        }
    }

    public void addPage(PageInfo info) {
        history.put(info.getName(), info);
    }

    public void show(PageInfo info, boolean affectHistory) {
      // Don't bother re-displaying the existing sink. This can be an issue
      // in practice, because when the history context is set, our
      // onHistoryChanged() handler will attempt to show the currently-visible
      // sink.
      if (info == curInfo) {
        return;
      }
      curInfo = info;
      
      // If affectHistory is set, create a new item on the history stack. This
      // will ultimately result in onHistoryChanged() being called. It will call
      // show() again, but nothing will happen because it will request the exact
      // same sink we're already showing.
      if (affectHistory) {
        History.newItem(info.getName());
      }

      // Display the page
      history.put(info.getName(), info);
      info.show();
    }
    
    public void onHistoryChanged(String token) {
        if (curInfo != null) {
            curInfo.getInstance().onHide();
        }
        
        PageInfo page = (PageInfo) history.get(token);
        
        if (page == null) {
            return;
        }
        
        int idx = page.getTabIndex();
        if (idx >= 0 && idx < tabPanel.getWidgetCount()) {
            tabPanel.selectTab(page.getTabIndex());
        }
        page.show();
        page.getInstance().onShow();
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }

    public UserServiceAsync getUserService() {
        return userService;
    }

    public TabPanel getTabPanel() {
        return tabPanel;
    }
}
