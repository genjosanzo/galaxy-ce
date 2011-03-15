/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.mule.galaxy.web.client;

import static org.mule.galaxy.web.client.ClientId.MAIN_WELCOME_MESSAGE_ID;
import static org.mule.galaxy.web.client.ClientId.TAB_HEADER_ADMINISTRATION_ID;
import static org.mule.galaxy.web.client.ui.panel.WidgetHelper.newSpacerPipe;

import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.ExternalHyperlink;
import org.mule.galaxy.web.client.ui.help.AdministrationConstants;
import org.mule.galaxy.web.client.ui.help.BaseConstants;
import org.mule.galaxy.web.client.ui.help.BaseMessages;
import org.mule.galaxy.web.client.ui.panel.AboutPanel;
import org.mule.galaxy.web.client.ui.panel.InlineFlowPanel;
import org.mule.galaxy.web.client.ui.panel.PluginPanel;
import org.mule.galaxy.web.client.ui.panel.WidgetHelper;
import org.mule.galaxy.web.rpc.AdminService;
import org.mule.galaxy.web.rpc.AdminServiceAsync;
import org.mule.galaxy.web.rpc.ApplicationInfo;
import org.mule.galaxy.web.rpc.GalaxyService;
import org.mule.galaxy.web.rpc.GalaxyServiceAsync;
import org.mule.galaxy.web.rpc.HeartbeatService;
import org.mule.galaxy.web.rpc.HeartbeatServiceAsync;
import org.mule.galaxy.web.rpc.PluginTabInfo;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;
import org.mule.galaxy.web.rpc.WExtensionInfo;
import org.mule.galaxy.web.rpc.WUser;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Galaxy {

    private String firstPage = "browse";
    private SecurityServiceAsync securityService;
    private HeartbeatServiceAsync heartbeatService;
    private AdminServiceAsync adminService;
    private InlineFlowPanel rightHeaderPanel;
    protected WUser user;
    protected int adminTabIndex;
    protected Viewport base;
    protected List<WExtensionInfo> extensions;
    protected Label product;
    protected InlineFlowPanel footerPanel;

    protected int repositoryTabIndex;
    private BaseConstants baseConstants;
    private BaseMessages baseMessages;

    private AdministrationConstants administrationConstants;
    protected Collection<PluginTabInfo> plugins;
    protected boolean userManagementSupported;
    private PageManager pageManager;
    private GalaxyServiceAsync galaxyService;
    private AdministrationPanel adminPanel;
    private String logoHref = "images/galaxy_logo_main_trans.gif";
    private InlineFlowPanel alertNotificationArea;
    private ApplicationInfo applicationInfo;

    public void initialize(final List<GalaxyModule> modules) {
        //GXT.setDefaultTheme(Theme.GRAY, true);
        GXT.BLANK_IMAGE_URL = "gxtresources/images/default/s.gif";
        
        this.galaxyService = (GalaxyServiceAsync) GWT.create(GalaxyService.class);

        ServiceDefTarget target = (ServiceDefTarget) galaxyService;
        String baseUrl = GWT.getModuleBaseURL();
        target.setServiceEntryPoint(baseUrl + "../handler/galaxyService.rpc");

        this.securityService = (SecurityServiceAsync) GWT.create(SecurityService.class);

        target = (ServiceDefTarget) securityService;
        target.setServiceEntryPoint(baseUrl + "../handler/securityService.rpc");

        this.heartbeatService = (HeartbeatServiceAsync) GWT.create(HeartbeatService.class);
        target = (ServiceDefTarget) heartbeatService;
        target.setServiceEntryPoint(baseUrl + "../handler/heartbeat.rpc");

        this.adminService = (AdminServiceAsync) GWT.create(AdminService.class);
        target = (ServiceDefTarget) adminService;
        target.setServiceEntryPoint(baseUrl + "../handler/admin.rpc");

        this.baseConstants = (BaseConstants) GWT.create(BaseConstants.class);
        this.baseMessages = (BaseMessages) GWT.create(BaseMessages.class);

        this.administrationConstants = (AdministrationConstants) GWT.create(AdministrationConstants.class);
        base = new Viewport();
        base.setBorders(false);
        base.setLayout(new BorderLayout());

        createHeader();

        this.pageManager = new PageManager();

        createBody();
        createFooter();

        RootPanel.get().add(base);
        
        galaxyService.getApplicationInfo(new AsyncCallback<ApplicationInfo>() {
            
            public void onFailure(Throwable e) {
                GWT.log("Could not load application.", e);
            }

            public void onSuccess(ApplicationInfo appInfo) {
                initializeApplication(appInfo, modules);
            }
        });

        new HeartbeatTimer(Galaxy.this);

        // prefetch the image, so that e.g. SessionKilled dialog can be properly displayed for the first time
        // when the server is already down and cannot serve it.
        Image.prefetch("images/lightbox.png");
    }

    protected void initializeApplication(ApplicationInfo appInfo, final List<GalaxyModule> modules) {
        this.applicationInfo = appInfo;
        
        user = (WUser) appInfo.getUser();
        // always the left most item
        final Label welcomeLabel = new Label("Welcome, " + user.getName());
        welcomeLabel.getElement().setId(MAIN_WELCOME_MESSAGE_ID);
        rightHeaderPanel.insert(welcomeLabel, 0);
        
        extensions = (List<WExtensionInfo>) appInfo.getExtensions();
        Collections.sort(extensions);
        
        plugins = appInfo.getPluginTabs();
        userManagementSupported = appInfo.isUserManagementSupported();
        adminPanel = createAdministrationPanel();
        
        for (GalaxyModule module : modules) {
            module.initialize(Galaxy.this);
        }
        
        loadTabs(Galaxy.this);
        showFirstPage();

        pageManager.initialize();
    }
    
    public ApplicationInfo getApplicationInfo() {
        return applicationInfo;
    }

    private void createFooter() {
        ContentPanel southPanel = new ContentPanel();
        southPanel.setBodyBorder(false);
        southPanel.setBorders(false);
        southPanel.setHeaderVisible(false);
        southPanel.addStyleName("footer-viewport");
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.SOUTH, 45);
        data.setMargins(new Margins());

        footerPanel = new InlineFlowPanel();
        footerPanel.setStyleName("footer");

        prependFooterConent();

        southPanel.add(footerPanel);
        base.add(southPanel, data);
    }

    /**
     * Adds to the left of the  copyright info.
     */
    protected void prependFooterConent() {
        product = new Label("About " + getProductName());
        product.setStyleName("footer-link footer-text");
        product.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                getAboutPanel();
            }
        });
        footerPanel.add(product);

        this.postProductInfoAdded();

        footerPanel.add(WidgetHelper.newSpacerPipe());

        HTML copyright = new HTML(getFooterText());
        footerPanel.add(copyright);
    }
    
    protected void postProductInfoAdded() {
    	
    }

    protected AboutPanel getAboutPanel() {
        return new AboutPanel();
    }

    protected void createHeader() {
        ContentPanel northPanel = new ContentPanel();
        northPanel.setBodyBorder(false);
        northPanel.setBorders(false);
        northPanel.setHeaderVisible(false);

        BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 33);
        data.setMargins(new Margins());
        data.setSize(50);

        rightHeaderPanel = new InlineFlowPanel();
        rightHeaderPanel.setStyleName("header-right");
        rightHeaderPanel.add(createHeaderOptions());

        // custom logo
        FlowPanel header = new FlowPanel();
        header.setStyleName("header");
        header.add(rightHeaderPanel);

        header.add(createLogo(logoHref));

        northPanel.add(header);
        base.add(northPanel, data);
    }

    protected final Image createLogo(final String url) {
        final Image logo = new Image(url);
        logo.setTitle("Home");
        logo.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent clickEvent) {
                onLogoClick(clickEvent);
            }
        });
        return logo;
    }

    /**
     * Intercepts click event on logo.
     */
    protected void onLogoClick(final ClickEvent clickEvent) {
        History.newItem("browse");
    }

    protected InlineFlowPanel createAlertNotificationArea() {
        alertNotificationArea = new InlineFlowPanel();
        alertNotificationArea.setStyleName("alert-header-right");
        return alertNotificationArea;
    }

    public InlineFlowPanel getAlertNotificationArea() {
        return alertNotificationArea;
    }

    protected InlineFlowPanel createHeaderOptions() {
        InlineFlowPanel options = new InlineFlowPanel();
        options.setStyleName("header-right-options");

        ExternalHyperlink logout = new ExternalHyperlink("Log Out", GWT.getHostPageBaseURL() + "j_logout");
        options.add(newSpacerPipe());
        options.add(logout);

        return options;
    }

    protected void createBody() {
        BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
        data.setMargins(new Margins());

        base.add(pageManager.getTabPanel(), data);
    }

    protected String getFooterText() {
        return "&copy; MuleSoft, Inc. All rights reserved";
    }

    protected native String getProductName()
    /*-{
        return $wnd.productName;
    }-*/;


    protected void loadTabs(final Galaxy galaxy) {
        loadPluginTabs();
        loadAdminTab();
    }

    protected void loadAdminTab() {
        if (showAdminTab()) {
            adminTabIndex = pageManager.createTab("Administration", "admin", administrationConstants.admin_TabTip(), TAB_HEADER_ADMINISTRATION_ID);
            pageManager.createPageInfo("admin", adminPanel, adminTabIndex);
        }
    }

    public AdministrationPanel getAdministrationPanel() {
        return adminPanel;
    }

    protected void loadPluginTabs() {
        for (PluginTabInfo plugin : getPlugins()) {
            int index = pageManager.createTab(plugin.getName(), plugin.getRootToken(), "");
            pageManager.createPageInfo(plugin.getRootToken(), new PluginPanel(plugin), index);
        }
    }

    protected Collection<PluginTabInfo> getPlugins() {
        return plugins;
    }

    protected AdministrationPanel createAdministrationPanel() {
        return new AdministrationPanel(this);
    }

    public boolean showAdminTab() {
        for (Iterator<String> itr = user.getPermissions().iterator(); itr.hasNext();) {
            String s = itr.next();

            if (s.startsWith("MANAGE_") || "EXECUTE_ADMIN_SCRIPTS".equals(s)) {
                return true;
            }
        }
        return false;
    }

    protected void showFirstPage() {
        ///Remove the loading message
        DOM.setInnerHTML(RootPanel.get("loading-msg").getElement(), "");
        
        // Show the initial screen.
        String initToken = History.getToken();
        if (initToken != null && initToken.length() > 0) {
            try {
                pageManager.show(initToken);
            } catch (IllegalStateException e) {
                pageManager.show(getFirstPage());
            }
        } else {
            pageManager.show(getFirstPage());
        }
    }
  
    protected String getFirstPage() {
        return firstPage;
    }

    /**
     * just for informational messages.
     * should not be used for errors because this fades out.
     *
     * @param token
     * @param message
     */
    public void setInfoMessageAndGoto(String token, String message) {
        History.newItem(token);
        Info.display("Info:", message);
    }

    public PageManager getPageManager() {
        return pageManager;
    }

    public void setUserManagementSupported(boolean userManagementSupported) {
        this.userManagementSupported = userManagementSupported;
    }

    public boolean isUserManagementSupported() {
        return userManagementSupported;
    }

    public List<WExtensionInfo> getExtensions() {
        return extensions;
    }

    public SecurityServiceAsync getSecurityService() {
        return securityService;
    }

    public HeartbeatServiceAsync getHeartbeatService() {
        return this.heartbeatService;
    }

    public AdminServiceAsync getAdminService() {
        return adminService;
    }

    public TabPanel getTabPanel() {
        return pageManager.getTabPanel();
    }

    public BaseConstants getBaseConstants() {
        return baseConstants;
    }

    public BaseMessages getBaseMessages() {
        return baseMessages;
    }

    public boolean hasPermission(final String requiredPermission) {
        for (final String permission : user.getPermissions()) {
            if (permission.startsWith(requiredPermission)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param permissions
     * @return true if at least one of specified permissions applies
     */
    public boolean hasAtLeastOnePermission(final String ... permissions) {
        for (final String permission : permissions) {
            if (hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public int getAdminTab() {
        return adminTabIndex;
    }

    public WExtensionInfo getExtension(String id) {
        for (Iterator<WExtensionInfo> itr = extensions.iterator(); itr.hasNext();) {
            WExtensionInfo ei = itr.next();

            if (id.equals(ei.getId())) {
                return ei;
            }
        }
        return null;
    }

    public AdministrationConstants getAdministrationConstants() {
        return administrationConstants;
    }

    public void setMessageAndGoto(String successToken, String successMessage) {
        pageManager.setMessageAndGoto(successToken, successMessage);
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    public void setLogoHref(String logoHref) {
        this.logoHref = logoHref;
    }
}