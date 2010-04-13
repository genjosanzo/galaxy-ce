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

package org.mule.galaxy.repository.client.item;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.client.admin.PolicyPanel;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.web.client.ui.panel.AbstractFlowComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.ui.panel.ShowableTabListener;
import org.mule.galaxy.web.rpc.SecurityService;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.List;


/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 * (with history)
 * - View Artiact
 */
public class ItemPanel extends AbstractFlowComposite {

    private Galaxy galaxy;
    private ItemInfo info;
    private int selectedTab = -1;
    private String itemId;
    private List<String> params;
    private RepositoryMenuPanel menuPanel;
    private TabPanel tabPanel;
    private RepositoryModule repository;

    public ItemPanel(RepositoryMenuPanel menuPanel) {
        this.galaxy = menuPanel.getGalaxy();
        this.repository = menuPanel.getRepositoryModule();
        this.menuPanel = menuPanel;
    }

    @Override
    public void showPage(List<String> params) {
        this.params = params;
        panel.clear();
        panel.add(new Label("Loading..."));

        if (params.size() >= 2) {
            selectedTab = new Integer(params.get(1)).intValue();
        } else {
            selectedTab = 0;
        }
    }

    public void initializeItem(ItemInfo info) {
        this.info = info;
        panel.clear();
        initTabs();

        if (info.getType().equals("Artifact Version")) {
            tabPanel.setSelection(tabPanel.getItem(1));
        }
    }

    private void initTabs() {
        ContentPanel contentPanel = new ContentPanel();
        contentPanel.setAutoHeight(true);
        contentPanel.setAutoWidth(true);
        contentPanel.setBodyBorder(false);
        contentPanel.addStyleName("x-panel-container-full");

        tabPanel = new TabPanel();
        tabPanel.setStyleName("x-tab-panel-header_sub1");
        tabPanel.setAutoWidth(true);
        tabPanel.setAutoHeight(true);

        TabItem itemsTab = new TabItem("Items");
        itemsTab.getHeader().setToolTip(repository.getRepositoryConstants().repo_Items_TabTip());
        itemsTab.add(new ChildItemsPanel(galaxy, menuPanel, info));
        tabPanel.add(itemsTab);


        TabItem infoTab = new TabItem("Info");
        infoTab.getHeader().setToolTip(repository.getRepositoryConstants().repo_Info_TabTip());
        infoTab.add(new ItemInfoPanel(menuPanel, info, this, params));
        tabPanel.add(infoTab);

        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            TabItem tab = new TabItem("Policies");
            tab.getHeader().setToolTip(repository.getRepositoryConstants().repo_Policies_TabTip());
            tab.add(new PolicyPanel(menuPanel, galaxy, repository.getRegistryService(), itemId));
            tabPanel.add(tab);
        }

        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            TabItem tab = new TabItem("Security");
            tab.getHeader().setToolTip(repository.getRepositoryConstants().repo_Security_TabTip());
            tab.add(new ItemRolePermissionPanel(galaxy, menuPanel, info.getId(), SecurityService.ITEM_PERMISSIONS));
            tabPanel.add(tab);
        }

        /**
         * Lazily initialize the panels with the proper parameters.
         */
        tabPanel.addListener(Events.Select, new ShowableTabListener(tabPanel, menuPanel, "items/" + itemId, params, null));

        contentPanel.add(tabPanel);
        panel.add(contentPanel);

        if (selectedTab > -1) {
            tabPanel.setSelection(tabPanel.getItem(selectedTab));
        } else {
            tabPanel.setSelection(tabPanel.getItem(0));
        }
    }

    public String getItemId() {
        return itemId;
    }
}
