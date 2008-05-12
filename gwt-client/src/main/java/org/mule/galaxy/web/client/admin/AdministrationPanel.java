package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

public class AdministrationPanel extends MenuPanel {

    private final Galaxy galaxy;

    public AdministrationPanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;

        Toolbox manageBox = new Toolbox(false);
        manageBox.setTitle("Manage");
        addMenuItem(manageBox);

        if (galaxy.hasPermission("MANAGE_ARTIFACT_TYPES")) {
            createLinkWithAdd(manageBox, 
                              "Artifact Types", 
                              "aritfact-types", 
                              new ArtifactTypeListPanel(this),
                              new ArtifactTypeForm(this));
        }        

        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            createLinkWithAdd(manageBox, 
                              "Groups", 
                              "groups", 
                              new GroupListPanel(this),
                              new GroupForm(this));
        }
        
        if (galaxy.hasPermission("MANAGE_LIFECYCLES")) {
            createLinkWithAdd(manageBox, 
                              "Lifecycles", 
                              "lifecycles", 
                              new LifecycleListPanel(this),
                              new LifecycleForm(this));
        }
        
        if (galaxy.hasPermission("MANAGE_INDEXES")) {
            createLinkWithAdd(manageBox, 
                              "Indexes", 
                              "indexes", 
                              new IndexListPanel(this),
                              new IndexForm(this));
        }
        
        if (galaxy.hasPermission("MANAGE_POLICIES")) {
            Hyperlink link = new Hyperlink("Policies", "policies");
            createPageInfo(link.getTargetHistoryToken(), new PolicyPanel(this, galaxy));
            manageBox.add(link);
        }

        if (galaxy.hasPermission("MANAGE_PROPERTIES")) {
            createLinkWithAdd(manageBox, 
                              "Properties", 
                              "properties", 
                              new PropertyDescriptorListPanel(this),
                              new PropertyDescriptorForm(this));
        }

        if (galaxy.hasPermission("MANAGE_USERS")) {
            createLinkWithAdd(manageBox, 
                              "Users", 
                              "users", 
                              new UserListPanel(this),
                              new UserForm(this));
        }
    }

    private void createLinkWithAdd(Toolbox manageBox, 
                                   String title, 
                                   String tokenBase,
                                   AbstractComposite list,
                                   AbstractComposite form) {
        Hyperlink link = new Hyperlink(title, tokenBase);
        createPageInfo(link.getTargetHistoryToken(), list);
        
        createDivWithAdd(manageBox, link, new Hyperlink("Add", tokenBase + "/new"));
        createPageInfo(tokenBase + "/*", form);
    }

    private void createPageInfo(String token, final AbstractComposite composite) {
        final AdministrationPanel adminPanel = this;
        PageInfo page = new PageInfo(token, getGalaxy().getAdminTab()) {

            public AbstractComposite createInstance() {
                return null;
            }

            public AbstractComposite getInstance() {
                adminPanel.setMain(composite);
                return adminPanel;
            }
            
        };
        getGalaxy().addPage(page);
    }

    private void createDivWithAdd(Toolbox manageBox, Hyperlink link, Hyperlink add) {
        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));
   
        manageBox.add(item);
    }

    public void showUsers() {
        History.newItem("users");
    }

    public void showPropertyDescriptors() {
        History.newItem("properties");
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }
    
    public RegistryServiceAsync getRegistryService() {
        return getGalaxy().getRegistryService();
    }

    public SecurityServiceAsync getSecurityService() {
        return getGalaxy().getSecurityService();
    }
}
