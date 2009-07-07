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

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.ApplicationPanel;
import org.mule.galaxy.web.client.activity.ActivityPanel;
import org.mule.galaxy.web.client.util.Toolbox;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

import com.google.gwt.user.client.ui.Hyperlink;

public class AdministrationPanel extends ApplicationPanel {

    private final Galaxy galaxy;

    public AdministrationPanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
    }

    @Override
    protected void onFirstShow() {
        super.onFirstShow();

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

        if (galaxy.hasPermission("MANAGE_PROPERTIES")) {
            createLinkWithAdd(manageBox,
                    "Types",
                    "types",
                    new TypeListPanel(this),
                    new TypeForm(this));
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
        if (galaxy.hasPermission("VIEW_ACTIVITY")) {
            Hyperlink activityLink = new Hyperlink("Activity", "activity");
            createPageInfo(activityLink.getTargetHistoryToken(), new ActivityPanel(this, galaxy));
            utilityBox.add(activityLink);
        }

        Hyperlink adminLink = new Hyperlink("Admin Shell", "adminShell");
        createPageInfo(adminLink.getTargetHistoryToken(), new AdminShellPanel(this));
        utilityBox.add(adminLink);


        // Scheduler
        createLinkWithAdd(utilityBox,
                "Scheduler",
                "schedules",
                new ScheduleListPanel(this),
                new ScheduleForm(this));

    }

    protected void createPageInfo(String token, final AbstractComposite composite) {
        final AdministrationPanel aPanel = this;
        PageInfo page = new PageInfo(token, getGalaxy().getAdminTab()) {

            public AbstractComposite createInstance() {
                return null;
            }

            public AbstractComposite getInstance() {
                aPanel.setMain(composite);
                return aPanel;
            }

        };
        getGalaxy().addPage(page);
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
