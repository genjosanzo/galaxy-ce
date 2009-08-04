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

package org.mule.galaxy.web.client.item;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.google.gwt.user.client.ui.Label;

import java.util.List;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.util.ShowableTabListener;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.SecurityService;


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

    public ItemPanel(Galaxy galaxy, RepositoryMenuPanel menuPanel) {
        this.galaxy = galaxy;
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
        ContentPanel cp = new ContentPanel();
        cp.setStyleName("x-panel-container-full");
        cp.setBodyBorder(false);
        cp.setHeading(info.getName());
        cp.setAutoWidth(true);
        //cp.setLayout(new FitLayout());

        tabPanel = new TabPanel();
        tabPanel.setStyleName("x-tab-panel-header_sub1");
        tabPanel.setAutoWidth(true);
        tabPanel.setAutoHeight(true);

        TabItem itemsTab = new TabItem("Items");
        itemsTab.add(new ChildItemsPanel(galaxy, menuPanel, info));
        tabPanel.add(itemsTab);


        TabItem infoTab = new TabItem("Info");
        infoTab.add(new ItemInfoPanel(galaxy, menuPanel, info, this, params));
        tabPanel.add(infoTab);

        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            TabItem tab = new TabItem("Policies");
            tab.add(new PolicyPanel(menuPanel, galaxy, itemId));
            tabPanel.add(tab);
        }

        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            TabItem tab = new TabItem("Security");
            tab.add(new ItemRolePermissionPanel(galaxy, menuPanel, info.getId(), SecurityService.ITEM_PERMISSIONS));
            tabPanel.add(tab);
        }

        /**
         * Lazily initialize the panels with the proper parameters.
         */
        tabPanel.addListener(Events.Select, new ShowableTabListener(menuPanel, params));

        cp.add(tabPanel);
        panel.add(cp);

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
