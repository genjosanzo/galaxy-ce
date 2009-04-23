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

package org.mule.galaxy.web.client.entry;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.registry.RegistryMenuPanel;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.ConfirmDialog;
import org.mule.galaxy.web.client.util.ConfirmDialogAdapter;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.client.util.LightBox;
import org.mule.galaxy.web.client.util.NavigationUtil;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.SecurityService;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 *   (with history)
 * - View Artiact
 */
public class ItemPanel extends AbstractComposite {

    private Galaxy galaxy;
    private TabPanel tabs;
    private ItemInfo info;
    private VerticalPanel panel;
    private int selectedTab = -1;
    private RegistryMenuPanel menuPanel;
    private String itemId;

    private List<String> params;
    private FlowPanel browsePanel;
    private ColumnView cv;
    private TreeItem workspaceTreeItem;
    private Collection<ItemInfo> items;
    

    // this is the first time we're showing this workspace
    // so we'll also need to load the children
    private boolean loadChildren = true;
    private boolean first = true;

    public ItemPanel(Galaxy galaxy) {
        this.galaxy = galaxy;
        
        menuPanel = new RegistryMenuPanel(galaxy);
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        menuPanel.setMain(panel);
        
        initWidget(menuPanel);
    }
    
    public ErrorPanel getErrorPanel() {
        return menuPanel;
    }
    
    public void onShow(List<String> params) {
        this.params = params;
        menuPanel.clearErrorMessage();
        menuPanel.onShow();
        panel.clear();
        panel.add(new Label("Loading..."));
        
        if (params.size() > 0) {
            itemId = params.get(0);
        }

        if (params.size() >= 2) {
            selectedTab = new Integer(params.get(1)).intValue();
        } else {
            selectedTab = 0;
        }
        
        if (itemId != null) {
            fetchItem();
        }

        if (first) {
            FlowPanel browseToolbar = new FlowPanel();
            browseToolbar.setStyleName("toolbar");
    
            browsePanel = new FlowPanel();
            cv = new ColumnView();
            browsePanel.add(browseToolbar);
            browsePanel.add(cv);
            menuPanel.setTop(browsePanel);
            first = false;
        }
        
        refreshWorkspaces();
    }

    private void fetchItem() {
        AbstractCallback callback = new AbstractCallback(menuPanel) { 
            public void onSuccess(Object o) {
                info = (ItemInfo) o;
                
                init();
            }
        };
        
        galaxy.getRegistryService().getItemInfo(itemId, true, callback);
    }

    private void init() {
        panel.clear();
        tabs = new TabPanel();
        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        panel.add(getItemLinks());
        panel.add(tabs);
        
        initTabs();
    }


    public void refreshWorkspaces() {
        final TreeItem treeItem = new TreeItem();

        // Load the workspaces into a tree on the left
        galaxy.getRegistryService().getItems(itemId, new AbstractCallback(menuPanel) {

            @SuppressWarnings("unchecked")
            public void onSuccess(Object o) {
                items = (Collection<ItemInfo>) o;

                initItems(treeItem, items);

                if (itemId == null) {
                    TreeItem child = treeItem.getChild(0);
                    workspaceTreeItem = child;
                    itemId = (String) child.getUserObject();
                    fetchItem();
                }

                cv.setRootItem(treeItem, workspaceTreeItem);

                if (loadChildren) {
                    // this is the first load of the browse. This will trigger
                    // a load of the child workspaces of the selected item
                    loadChildren = false;
                    refreshWorkspaces();
                }
            }
        });
    }

    private void initItems(TreeItem ti, Collection<ItemInfo> workspaces) {
        for (ItemInfo wi : workspaces) {
            TreeItem treeItem = ti.addItem(wi.getName());
            treeItem.setUserObject(wi.getId());

            if (itemId != null && itemId.equals(wi.getId())) {
                workspaceTreeItem = treeItem;
            }

            Collection<ItemInfo> children = wi.getItems();
            if (children != null) {
                initItems(treeItem, children);
            }
        }
    }
    
    private void initTabs() {
        tabs.add(new ItemInfoPanel(galaxy, menuPanel, info, this, params), "Info");
        
        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            tabs.add(new PolicyPanel(menuPanel, galaxy, itemId), "Policies");
        }
        
        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            tabs.add(new ItemGroupPermissionPanel(galaxy, menuPanel, info.getId(), SecurityService.ITEM_PERMISSIONS), "Security");
        }
        
        if (selectedTab > -1) {
            tabs.selectTab(selectedTab);
        } else {
            tabs.selectTab(0);
        }

        tabs.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents events, int tab) {
                menuPanel.clearErrorMessage();
                AbstractComposite composite = (AbstractComposite) tabs.getWidget(tab);
                
                composite.onShow();
            }
            
        });
    }

    private Panel getItemLinks() {
        InlineFlowPanel linkPanel = new InlineFlowPanel();
        linkPanel.setStyleName("artifactLinkPanel");

        
        if (info.isModifiable()) {
            Image img = new Image("images/icon_copy.gif");
            img.setStyleName("icon-baseline");
            
            String token = "new-item/" + info.getId();
            img.addClickListener(NavigationUtil.createNavigatingClickListener(token));
            Hyperlink hl = new Hyperlink("New", token);

            linkPanel.add(asToolbarItem(img, hl));
        }
        
        if (info.isDeletable()) {
            ClickListener cl = new ClickListener() {
                public void onClick(Widget arg0) {
                    warnDelete();
                }
            };
            Image img = new Image("images/delete_config.gif");
            img.setStyleName("icon-baseline");
            img.addClickListener(cl);
            Hyperlink hl = new Hyperlink("Delete", "artifact/" + info.getId());
            hl.addClickListener(cl);
            linkPanel.add(asToolbarItem(img, hl));
        }

        ClickListener cl = new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getArtifactFeedLink(), null, "scrollbars=yes");
            }
        };
        
        Image img = new Image("images/feed-icon.png");
//        img.setStyleName("feed-icon");
        img.setTitle("Versions Atom Feed");
        img.addClickListener(cl);
        img.setStyleName("icon-baseline");
        
        Hyperlink hl = new Hyperlink("Feed", "artifact-versions/" + info.getId());
        hl.addClickListener(cl);
        linkPanel.add(asToolbarItem(img, hl));
        
        return linkPanel;
    }
    
    private Widget asToolbarItem(Image img, Widget hl) {
        InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
        p.setStyleName("artifactToolbarItem");
        
        return p;
    }

    protected void warnDelete()
    {
        new LightBox(new ConfirmDialog(new ConfirmDialogAdapter()
        {
            public void onConfirm()
            {
                galaxy.getRegistryService().delete(info.getId(), new AbstractCallback(menuPanel)
                {
                    public void onSuccess(Object arg0)
                    {
                        galaxy.setMessageAndGoto("browse", "Artifact was deleted.");
                    }
                });
            }
        }, "Are you sure you want to delete this artifact?")).show();
    }
}
