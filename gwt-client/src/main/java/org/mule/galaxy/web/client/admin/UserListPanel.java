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

import org.mule.galaxy.web.client.ui.help.InlineHelpPanel;
import org.mule.galaxy.web.client.util.FauxLinkRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WUser;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;

import java.util.ArrayList;
import java.util.List;

public class UserListPanel extends AbstractAdministrationComposite {
    public UserListPanel(AdministrationPanel a) {
        super(a);
    }

    @Override
    public void doShowPage() {
        super.doShowPage();
        adminPanel.getSecurityService().getUsers(new AbstractCallback<List<WUser>>(adminPanel) {

            public void onSuccess(List<WUser> users) {
                panel.clear();
                showUsers(users);
            }
        });
    }

    private void showUsers(List<WUser> users) {
        ContentPanel contentPanel = new ContentPanel();
        contentPanel.setHeading("Users");
        contentPanel.addStyleName("x-panel-container-full");
        contentPanel.setAutoWidth(true);
        contentPanel.setAutoHeight(true);
        contentPanel.setBodyBorder(false);

        // add inline help string and widget
        contentPanel.setTopComponent(
                new InlineHelpPanel(adminPanel.getGalaxy().getAdministrationConstants().admin_Users_Tip(), 19));


        BeanModelFactory factory = BeanModelLookup.get().getFactory(WUser.class);

        List<BeanModel> list = factory.createModel(users);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);
        ColumnConfig userConfig = new ColumnConfig("username", "Username", 100);
        userConfig.setRenderer(new FauxLinkRenderer());
        columns.add(userConfig);
        columns.add(new ColumnConfig("name", "Name", 200));
        columns.add(new ColumnConfig("email", "Email Address", 200));
        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setStripeRows(true);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.setAutoHeight(true);
        grid.setAutoExpandColumn("email");
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WUser s = store.getAt(ge.getRowIndex()).getBean();
                History.newItem("users/" + s.getId());
            }
        });

        // search filter
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store, BeanModel parent,
                                       BeanModel record, String property, String filter) {

                String name = record.get("name");
                name = name.toLowerCase();

                String username = record.get("username");
                username = username.toLowerCase();

                String email = record.get("email");
                email = email.toLowerCase();

                if (name.indexOf(filter.toLowerCase()) != -1 ||
                        username.indexOf(filter.toLowerCase()) != -1 ||
                        email.indexOf(filter.toLowerCase()) != -1) {
                    return true;
                }
                return false;
            }
        };

        filter.setName("Search");
        filter.setFieldLabel("Search");
        filter.setWidth(300);
        filter.setTriggerStyle("x-form-search-trigger");
        filter.addStyleName("x-form-search-field");
        // Bind the filter field to your grid store (grid.getStore())
        filter.bind(store);

        ToolBar toolbar = new ToolBar();
        toolbar.add(filter);
        toolbar.add(new FillToolItem());

        String buttonTip = adminPanel.getGalaxy().getAdministrationConstants().admin_Users_New();
        toolbar.add(createToolbarHistoryButton("New", "users/new", buttonTip));

        contentPanel.add(toolbar);
        contentPanel.add(grid);

        panel.add(contentPanel);

    }

}
