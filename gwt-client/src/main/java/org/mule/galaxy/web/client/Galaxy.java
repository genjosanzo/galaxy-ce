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
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Iterator;
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
//                if (oldTab != newTab) {
//                    if (curInfo != null) {
//                        history.put("tab-" + oldTab, curInfo);
//                    }
//                }
                return true;
            }

            public void onTabSelected(SourcesTabEvents tabPanel, int tab) {
                if (oldTab != tab && !suppressTabHistory) {
                    History.newItem("tab-" + tab);
                }
                oldTab = tab;
            }
            
        });
        
        createPageInfoForMenuPanel(0);
        createPageInfoForActivity(1);
        
        final Galaxy galaxy = this;
        registryService.getUserInfo(new AbstractCallback(registryPanel) {
            public void onSuccess(Object o) {
                user = (WUser) o;
                rightPanel.add(new Label(user.getName()));
                
                HTML logout = new HTML("<a href=\"" + GWT.getHostPageBaseURL() + "j_logout\">Logout</a>");
                rightPanel.add(logout);
                
                if (showAdminTab(user)) {
                    tabPanel.add(new AdministrationPanel(galaxy), "Administration");
                    createPageInfoForMenuPanel(2);
                    showFirstPage();
                }
            }
        });
        
        Label footer = new Label("Mule Galaxy, Copyright 2008 MuleSource, Inc.");
        footer.setStyleName("footer");
        base.add(footer);
        RootPanel.get().add(base);
    }
    
    protected boolean showAdminTab(WUser user) {
        for (Iterator itr = user.getPermissions().iterator(); itr.hasNext();) {
            String s = (String)itr.next();
            
            if (s.startsWith("manage_")) return true;
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

    public UserServiceAsync getUserService() {
        return userService;
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
