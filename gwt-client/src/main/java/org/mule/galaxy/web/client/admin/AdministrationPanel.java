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

import org.mule.galaxy.web.client.AbstractShowable;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.NavMenuItem;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.client.activity.ActivityPanel;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.SecurityServiceAsync;

import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.user.client.History;

import java.util.ArrayList;
import java.util.List;

public class AdministrationPanel extends MenuPanel {

    private final Galaxy galaxy;
    protected ContentPanel accordionPanel;
    protected List<NavMenuItem> ManageItems;
    protected List<NavMenuItem> utilityItems;

    public AdministrationPanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
    }

    @Override
    protected void onFirstShow() {
        super.onFirstShow();

        accordionPanel = createAccodionWrapperPanel();

        // list of all items for this panel
        ManageItems = fetchManageMenuItems(this.galaxy);
        accordionPanel.add(createPanelWithListView("Manage", ManageItems));

        utilityItems = fetchUtilityItems(this.galaxy);
        accordionPanel.add(createPanelWithListView("Utility", utilityItems));
        addMenuItem(accordionPanel);

        /*
        Toolbox manageBox = new Toolbox(false);
        manageBox.setTitle("Manage");
        addMenuItem(manageBox);
        createMenuItems(this.galaxy, manageBox);

        Toolbox utilityBox = new Toolbox(false);
        utilityBox.setTitle("Utilities");
        addMenuItem(utilityBox);
        createUtilityMenuItems(this.galaxy, utilityBox);
         */
    }


    /**
     * Also does the createPageInfo calls..
     *
     * @param heading
     * @param items
     * @return
     */
    protected ContentPanel createPanelWithListView(String heading, List<NavMenuItem> items) {
        ContentPanel c = new ContentPanel();
        c.setHeading(heading);
        c.setAutoHeight(true);
        c.setAutoHeight(true);

        // store for all menu items in container
        ListStore<NavMenuItem> ls = new ListStore<NavMenuItem>();
        ls.add(items);

        ListView<NavMenuItem> lv = new ListView<NavMenuItem>();
        lv.setDisplayProperty("title"); // from item
        lv.setStore(ls);

        Menu contextMenu = new Menu();
        contextMenu.setWidth(100);

        for (final NavMenuItem item : ls.getModels()) {

            // handle page creation for list forms
            createPageInfo(item.getTokenBase(), item.getListPanel());

            // we could add a contextual menul item for each add
            if (item.getFormPanel() != null) {

                // handle page infor creation for add forms
                createPageInfo(item.getTokenBase() + "/" + Galaxy.WILDCARD, item.getFormPanel());

                MenuItem insert = new MenuItem();
                insert.setText("Add " + item.getTitle());
                insert.addSelectionListener(new SelectionListener<MenuEvent>() {
                    public void componentSelected(MenuEvent ce) {
                        History.newItem(item.getTokenBase() + NavMenuItem.NEW);
                    }
                });
                contextMenu.add(insert);
            }

            lv.addListener(Events.Select, new Listener<BaseEvent>() {
                public void handleEvent(BaseEvent be) {
                    ListViewEvent lve = (ListViewEvent) be;
                    NavMenuItem nmi = (NavMenuItem) lve.getModel();
                    History.newItem(nmi.getTokenBase());
                }
            });

            // double click gives us the "add form"
            // ... but who would figure that out?
            if (item.getFormPanel() != null) {
                lv.addListener(Events.DoubleClick, new Listener<BaseEvent>() {
                    public void handleEvent(BaseEvent be) {
                        ListViewEvent lve = (ListViewEvent) be;
                        NavMenuItem nmi = (NavMenuItem) lve.getModel();
                        History.newItem(nmi.getTokenBase() + NavMenuItem.NEW);
                    }
                });
            }

        }
        lv.setContextMenu(contextMenu);
        c.add(lv);
        return c;
    }


    protected List<NavMenuItem> fetchUtilityItems(Galaxy galaxy) {
        ArrayList a = new ArrayList();
        if (galaxy.hasPermission("VIEW_ACTIVITY")) {
            a.add(new NavMenuItem("Activity",
                    "ActivityPanel",
                    new ActivityPanel(this, galaxy),
                    null));
        }

        a.add(new NavMenuItem("Admin Shell",
                "adminShell",
                new AdminShellPanel(this),
                null));

        a.add(new NavMenuItem("Scheduler",
                "schedules",
                new ScheduleListPanel(this),
                new ScheduleForm(this)));

        return a;
    }

    protected List<NavMenuItem> fetchManageMenuItems(Galaxy galaxy) {
        ArrayList a = new ArrayList();

        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            a.add(new NavMenuItem("Groups",
                    "groups",
                    new GroupListPanel(this),
                    new GroupForm(this)));
        }
        if (galaxy.hasPermission("MANAGE_LIFECYCLES")) {
            a.add(new NavMenuItem("Lifecycles",
                    "lifecycles",
                    new LifecycleListPanel(this),
                    new LifecycleForm(this)));
        }

        if (galaxy.hasPermission("MANAGE_POLICIES")) {
            a.add(new NavMenuItem("Policies",
                    "policies",
                    new PolicyPanel(this, galaxy),
                    null));
        }

        if (galaxy.hasPermission("MANAGE_PROPERTIES")) {
            a.add(new NavMenuItem("Properties",
                    "properties",
                    new PropertyDescriptorListPanel(this),
                    new PropertyDescriptorForm(this)));
        }

        if (galaxy.hasPermission("MANAGE_PROPERTIES")) {
            a.add(new NavMenuItem("Types",
                    "types",
                    new TypeListPanel(this),
                    new TypeForm(this)));
        }

        if (galaxy.hasPermission("MANAGE_USERS")) {
            a.add(new NavMenuItem("Users",
                    "users",
                    new UserListPanel(this),
                    new UserForm(this)));
        }

        return a;
    }

    /*
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

    */

    protected void createPageInfo(String token, final WidgetHelper composite) {
        final AdministrationPanel aPanel = this;
        PageInfo page = new PageInfo(token, getGalaxy().getAdminTab()) {

            public AbstractShowable createInstance() {
                return null;
            }

            public AbstractShowable getInstance() {
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

    /*
     * root panel to add other panels too
     */
    protected ContentPanel createAccodionWrapperPanel() {
        AccordionLayout alayout = new AccordionLayout();
        accordionPanel = new ContentPanel();
        accordionPanel.setBodyBorder(false);
        accordionPanel.setHeaderVisible(false);
        accordionPanel.setLayout(alayout);
        return accordionPanel;
    }


    public ContentPanel getAccordionPanel() {
        return accordionPanel;
    }

    public void setAccordionPanel(ContentPanel accordionPanel) {
        this.accordionPanel = accordionPanel;
    }

    public List<NavMenuItem> getManageItems() {
        return ManageItems;
    }

    public void setManageItems(List<NavMenuItem> manageItems) {
        ManageItems = manageItems;
    }


    public List<NavMenuItem> getUtilityItems() {
        return utilityItems;
    }

    public void setUtilityItems(List<NavMenuItem> utilityItems) {
        this.utilityItems = utilityItems;
    }


}
