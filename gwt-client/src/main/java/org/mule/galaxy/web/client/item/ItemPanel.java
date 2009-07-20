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

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.web.client.AbstractShowable;
import org.mule.galaxy.web.client.AbstractWithTopComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.util.ColumnView;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
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
public class ItemPanel extends AbstractWithTopComposite {

    private Galaxy galaxy;
    private TabPanel tabs;
    private ItemInfo info;
    private VerticalPanel panel;
    private int selectedTab = -1;
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
    private FlowPanel browseToolbar;

    public ItemPanel(Galaxy galaxy) {
        this.galaxy = galaxy;

        FlowPanel main = getMainPanel();
        panel = new VerticalPanel();
        panel.setWidth("100%");
        main.add(panel);
        initWidget(main);
    }

    @Override
    public void showPage(List<String> params) {
        this.params = params;
        clearErrorMessage();
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

            FlowPanel top = new FlowPanel();


            browsePanel = new FlowPanel();
            browseToolbar = new InlineFlowPanel();
            browseToolbar.setStyleName("artifactLinkPanel");
            browsePanel.add(browseToolbar);

            FlowPanel cvPanel = new FlowPanel();
            cv = new ColumnView();
            cvPanel.add(cv);

            top.add(browseToolbar);
            top.add(cvPanel);
            setTop(top);
            first = false;
        }

        refreshWorkspaces();
    }

    private void fetchItem() {
        AbstractCallback callback = new AbstractCallback(this) {
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

        panel.add(tabs);
        initLinks();
        initTabs();
    }


    public void refreshWorkspaces() {
        final TreeItem treeItem = new TreeItem();

        // Load the workspaces into a tree on the left
        galaxy.getRegistryService().getItems(itemId, new AbstractCallback(this) {

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
        tabs.add(new ItemInfoPanel(galaxy, this, info, this, params), "Info");

        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            tabs.add(new PolicyPanel(this, galaxy, itemId), "Policies");
        }

        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            tabs.add(new ItemGroupPermissionPanel(galaxy, this, info.getId(), SecurityService.ITEM_PERMISSIONS), "Security");
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
                ItemPanel.this.clearErrorMessage();
                AbstractShowable composite = (AbstractShowable) tabs.getWidget(tab);

                composite.showPage(new ArrayList<String>());
            }

        });
    }

    public void initLinks() {
        browseToolbar.clear();

        // add item

        String style = "artifactToolbarItemFirst";

        if (info.isModifiable()) {
            Image addImg = new Image("images/add_obj.gif");
            addImg.addClickListener(new ClickListener() {
                public void onClick(Widget w) {
                    w.addStyleName("gwt-Hyperlink");

                    History.newItem("add-item/" + info.getId());
                }
            });

            Hyperlink addLink = new Hyperlink("New", "add-item/" + info.getId());
            InlineFlowPanel p = asHorizontal(addImg, new Label(" "), addLink);
            p.setStyleName(style);
            style = "artifactToolbarItem";
            browseToolbar.add(p);
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

            InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
            p.setStyleName(style);
            style = "artifactToolbarItem";
            browseToolbar.add(p);
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

        Hyperlink hl = new Hyperlink("Feed", "feed/" + info.getId());
        hl.addClickListener(cl);

        InlineFlowPanel p = asHorizontal(img, new Label(" "), hl);
        p.setStyleName(style);
        style = "artifactToolbarItem";
        browseToolbar.add(p);

        // spacer to divide the actions
        SimplePanel spacer = new SimplePanel();
        spacer.addStyleName("hr");
        browseToolbar.add(spacer);
    }

    protected void warnDelete() {

        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    galaxy.getRegistryService().delete(info.getId(), new AbstractCallback(ItemPanel.this) {
                        public void onSuccess(Object arg0) {
                            galaxy.setMessageAndGoto("browse", "Item was deleted.");
                        }
                    });
                }
            }
        };

        MessageBox.confirm("Confirm", "Are you sure you want to delete this item?", l);
    }

}
