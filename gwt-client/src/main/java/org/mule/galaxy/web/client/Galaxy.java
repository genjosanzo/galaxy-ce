package org.mule.galaxy.web.client;

import org.mule.galaxy.web.client.activity.ActivityPanel;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.util.ExternalHyperlink;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.RegistryService;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WUser;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy implements EntryPoint, HistoryListener {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private SecurityServiceAsync securityService;
    private FlowPanel rightPanel;
    private PageInfo curInfo;
    private Map history = new HashMap();
    private TabPanel tabPanel;
    private WUser user;
    protected int oldTab;
    private boolean suppressTabHistory;
    
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        History.addHistoryListener(this);
        
        this.registryService = (RegistryServiceAsync) GWT.create(RegistryService.class);
        
        ServiceDefTarget target = (ServiceDefTarget) registryService;
        target.setServiceEntryPoint(GWT.getModuleBaseURL() + "../handler/registry.rpc");
        
        this.securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);
        
        target = (ServiceDefTarget) securityService;
        target.setServiceEntryPoint(GWT.getModuleBaseURL() + "../handler/securityService.rpc");
        
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

        tabPanel.setStyleName("headerTabPanel");
        tabPanel.getDeckPanel().setStyleName("headerTabDeckPanel");
        tabPanel.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents event, int newTab) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents tabPanel, int tab) {
                if (oldTab != tab && !suppressTabHistory) {
                    History.newItem("tab-" + tab);
                }
                oldTab = tab;
            }
        });
        base.add(tabPanel);
        
        final Galaxy galaxy = this;
        registryService.getUserInfo(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                user = (WUser) o;
                rightPanel.add(new Label(user.getName()));

                ExternalHyperlink logout = new ExternalHyperlink("Logout", GWT.getHostPageBaseURL() + "j_logout");
                rightPanel.add(logout);
                
                loadTabs(galaxy);
            }

        });
        
        
        Label footer = new Label("Mule Galaxy, Copyright 2008 MuleSource, Inc.");
        footer.setStyleName("footer");
        base.add(footer);
        RootPanel.get().add(base);
    }

    protected void loadTabs(final Galaxy galaxy) {
        tabPanel.insert(registryPanel, "Registry", 0);
        createPageInfoForMenuPanel(0);
        
        if (hasPermission("VIEW_ACTIVITY")) {
            final ActivityPanel activityPanel = new ActivityPanel(this);
            tabPanel.insert(activityPanel, "Activity", tabPanel.getWidgetCount());
            createPageInfoForActivity(1);
        }
        
        if (showAdminTab(user)) {
            tabPanel.add(new AdministrationPanel(galaxy), "Administration");
            createPageInfoForMenuPanel(tabPanel.getWidgetCount() - 1);
        }
        showFirstPage();
    }
    
    protected boolean showAdminTab(WUser user) {
        for (Iterator itr = user.getPermissions().iterator(); itr.hasNext();) {
            String s = (String)itr.next();
            
            if (s.startsWith("MANAGE_")) return true;
        }
        return false;
    }

    private void createPageInfoForMenuPanel(int i) {
        final AbstractMenuPanel menuPanel = (AbstractMenuPanel) tabPanel.getWidget(i);
        Widget main = menuPanel.getMain();
        
        if (main == null) {
            main = new Label("");
        }
        
        final Widget fMain = main;
        
        PageInfo page = new PageInfo("tab-" + i, i) {
            public AbstractComposite createInstance() {
                return menuPanel;
            }

            public void show() {
                menuPanel.setMain(fMain);
                menuPanel.onShow();
            }
        };
        
        history.put(page.getName(), page);
    }
    
    private void createPageInfoForActivity(int i) {
        final AbstractComposite ac = (AbstractComposite) tabPanel.getWidget(i);
        PageInfo page = new PageInfo("tab-" + i, i) {
            public AbstractComposite createInstance() {
                return ac;
            }

            public void show() {
                ac.onShow();
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
            ((AbstractComposite) tabPanel.getWidget(0)).onShow();
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
        suppressTabHistory = true;
        curInfo = info;

        // If affectHistory is set, create a new item on the history stack. This
        // will ultimately result in onHistoryChanged() being called. It will
        // call
        // show() again, but nothing will happen because it will request the
        // exact
        // same sink we're already showing.
        if (affectHistory) {
            History.newItem(info.getName());
        }

        // Display the page
        history.put(info.getName(), info);
        info.show();
        suppressTabHistory = false;
    }
    
    public void onHistoryChanged(String token) {
        suppressTabHistory = true;
        if (token == "") {
            token = "tab-0";
        }
        
        if ("nohistory".equals(token) && curInfo != null)
        {
            suppressTabHistory = false;
            return;
        }
        
        PageInfo page = (PageInfo) history.get(token);
        
        if (page == null) {
            // went to a page which isn't in our history anymore. go to the first page
            if (curInfo == null) {
                onHistoryChanged("tab-0");
            }
            return;
        }
        
        if (curInfo != null) {
            curInfo.getInstance().onHide();
        }
        
        curInfo = page;
        
        int idx = page.getTabIndex();
        if (idx >= 0 && idx < tabPanel.getWidgetCount()) {
            tabPanel.selectTab(page.getTabIndex());
        }
        page.show();
        page.getInstance().onShow();
        suppressTabHistory = false;
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }

    public SecurityServiceAsync getSecurityService() {
        return securityService;
    }

    public TabPanel getTabPanel() {
        return tabPanel;
    }

    public boolean hasPermission(String perm) {
        for (Iterator itr = user.getPermissions().iterator(); itr.hasNext();) {
            String s = (String)itr.next();
            
            if (s.startsWith(perm)) return true;
        }
        return false;
    }
}
