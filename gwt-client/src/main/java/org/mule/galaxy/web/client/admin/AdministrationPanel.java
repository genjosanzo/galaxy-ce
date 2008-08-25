/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-01-29 00:59:35Z andrew $
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

package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

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
        createMenuItems(this.galaxy, manageBox);

        Toolbox utilityBox = new Toolbox(false);
        utilityBox.setTitle("Utilities");
        addMenuItem(utilityBox);
        createUtilityMenuItems(this.galaxy, utilityBox);

    }

    protected void createMenuItems(Galaxy galaxy, Toolbox manageBox) {
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

    protected void createUtilityMenuItems(Galaxy galaxy, Toolbox utilityBox) {

        Hyperlink adminLink = new Hyperlink("Admin Shell", "adminShell");
        createPageInfo(adminLink.getTargetHistoryToken(), new AdminShellPanel(this));
        utilityBox.add(adminLink);

        // Scheduler
        createLinkWithAdd(utilityBox,
                          "Scheduler",
                          "schedule",
                          new ScheduleListPanel(this),
                          new ScheduleForm(this));

    }

    protected void createLinkWithAdd(Toolbox manageBox,
                                   String title, 
                                   String tokenBase,
                                   AbstractComposite list,
                                   AbstractComposite form) {

        Hyperlink link = new Hyperlink(title, tokenBase);
        Hyperlink addLink = new Hyperlink("Add", tokenBase + "/new");

        createDivWithAdd(manageBox, link, addLink);
        createPageInfo(tokenBase, list);
        createPageInfo(tokenBase + "/" + Galaxy.WILDCARD, form);
    }

    protected void createPageInfo(String token, final AbstractComposite composite) {
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

    protected void createDivWithAdd(Toolbox manageBox, Hyperlink link, Hyperlink add) {
        InlineFlowPanel item = new InlineFlowPanel();
        item.add(link);
        item.add(new Label(" ["));
        item.add(add);
        item.add(new Label("]"));
   
        manageBox.add(item);
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
