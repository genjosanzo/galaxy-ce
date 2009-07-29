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

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.ErrorPanel;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.util.ShowableTabListener;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;
import org.mule.galaxy.web.rpc.SecurityService;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.Label;

import java.util.ArrayList;
import java.util.Collection;
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
    private Collection<ItemInfo> items;
    private int selectedTab = -1;
    private String itemId;
    private List<String> params;
    private ErrorPanel errorPanel;

    public ItemPanel(Galaxy galaxy, ErrorPanel errorPanel) {
        this.galaxy = galaxy;
        this.errorPanel = errorPanel;
    }

    @Override
    public void showPage(List<String> params) {
        this.params = params;
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
            fetchAllItems();
        }

    }

    private void fetchItem() {
        AbstractCallback callback = new AbstractCallback(errorPanel) {
            public void onSuccess(Object o) {
                info = (ItemInfo) o;
                init();
            }
        };

        galaxy.getRegistryService().getItemInfo(itemId, true, callback);
    }


    private void fetchAllItems() {
        AbstractCallback callback = new AbstractCallback(errorPanel) {
            public void onSuccess(Object o) {
                items = (Collection) o;
            }
        };

        galaxy.getRegistryService().getItems(itemId, callback);
    }

    private void init() {
        panel.clear();
        initTabs();
    }


    private void initTabs() {
        ContentPanel cp = new ContentPanel();
        cp.setStyleName("x-panel-container-full");
        cp.setBodyBorder(false);
        cp.setHeading(info.getName());
        cp.setAutoWidth(true);

        final TabPanel tabPanel = new TabPanel();
        tabPanel.setStyleName("x-tab-panel-header_sub1");
        tabPanel.setAutoWidth(true);
        tabPanel.setAutoHeight(true);

        TabItem itemsTab = new TabItem("Items");
        itemsTab.add(createItemGrid(info, items));
        tabPanel.add(itemsTab);


        TabItem infoTab = new TabItem("Info");
        infoTab.add(new ItemInfoPanel(galaxy, errorPanel, info, this, params));
        tabPanel.add(infoTab);

        if (galaxy.hasPermission("MANAGE_POLICIES") && info.isLocal()) {
            TabItem tab = new TabItem("Policies");
            tab.add(new PolicyPanel(errorPanel, galaxy, itemId));
            tabPanel.add(tab);
        }

        if (galaxy.hasPermission("MANAGE_GROUPS") && info.isLocal()) {
            TabItem tab = new TabItem("Security");
            tab.add(new ItemGroupPermissionPanel(galaxy, errorPanel, info.getId(), SecurityService.ITEM_PERMISSIONS));
            tabPanel.add(tab);
        }

        /**
         * Lazily initialize the panels with the proper parameters.
         */
        tabPanel.addListener(Events.Select, new ShowableTabListener(params));

        cp.add(tabPanel);
        panel.add(cp);

        if (selectedTab > -1) {
            tabPanel.setSelection(tabPanel.getItem(selectedTab));
        } else {
            tabPanel.setSelection(tabPanel.getItem(0));
        }
    }


    /**
     * generic grid for itemInfo
     *
     * @param info
     * @return
     */
    private ContentPanel createItemGrid(final ItemInfo info, final Collection items) {

        ContentPanel cp = new ContentPanel();
        cp.setHeaderVisible(false);
        cp.setAutoWidth(true);

        ToolBar toolbar = new ToolBar();
        cp.setTopComponent(toolbar);

        toolbar.add(new FillToolItem());
        if (info.isModifiable()) {
            toolbar.add(WidgetHelper.createSimpleHistoryButton("New Item", "add-item/" + info.getId()));
        }


        final CheckBoxSelectionModel<BeanModel> sm = new CheckBoxSelectionModel<BeanModel>();

        BeanModelFactory factory = BeanModelLookup.get().getFactory(ItemInfo.class);
        List<BeanModel> model = factory.createModel(items);

        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(model);

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(sm.getColumn());
        columns.add(new ColumnConfig("name", "Name", 100));
        columns.add(new ColumnConfig("path", "Path", 200));
        columns.add(new ColumnConfig("parentPath", "Parent", 150));
        columns.add(new ColumnConfig("authorName", "Author", 150));
        columns.add(new ColumnConfig("type", "Type", 100));

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setAutoWidth(true);
        grid.setSelectionModel(sm);
        grid.setBorders(true);
        grid.addPlugin(sm);
        grid.setAutoExpandColumn("name");

        grid.setAutoWidth(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                // any non checkbox...
                if (ge.getColIndex() > 0) {
                    ItemInfo ii = store.getAt(ge.getRowIndex()).getBean();
                    History.newItem("item/" + ii.getId());
                }
            }
        });

        final Button delBtn = new Button("Delete Item");
        delBtn.setEnabled(false);
        delBtn.setEnabled(false);
        delBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                warnDelete(sm.getSelectedItems());
                //History.newItem("artifact/" + info.getId());
            }
        });
        if (info.isDeletable()) {
            toolbar.add(delBtn);
        }
        
        sm.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
            public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
                boolean isSelected = sm.getSelectedItems().size() > 0;
                delBtn.setEnabled(isSelected);
            }
        });


        cp.add(grid);
        return cp;

    }


    protected void warnDelete(final List itemlist) {
        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {

                    // FIXME: delete collection.
                    /*galaxy.getRegistryService().delete(item.getId(), new AbstractCallback(errorPanel) {
                        public void onSuccess(Object arg0) {
                            galaxy.setMessageAndGoto("browse", "Item was deleted.");
                        }
                    });
                    */
                }
            }
        };
        MessageBox.confirm("Confirm", "Are you sure you want to delete these items?", l);
    }

    /*
    public void createToolbar() {

        ClickHandler cl = new ClickHandler() {

            public void onClick(ClickEvent sender) {
                Window.open(info.getArtifactFeedLink(), null, "scrollbars=yes");
            }
        };

        Image img = new Image("images/feed-icon.png");
        img.setTitle("Versions Atom Feed");
        img.addClickHandler(cl);
        img.setStyleName("icon-baseline");

        Hyperlink hl = new Hyperlink("Feed", "feed/" + info.getId());
        hl.addClickHandler(cl);

    }
        */

}
