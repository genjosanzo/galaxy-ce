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

import org.mule.galaxy.web.client.util.ListCellRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WLifecycle;
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
import java.util.Collection;
import java.util.List;

public class LifecycleListPanel extends AbstractAdministrationComposite {

    public LifecycleListPanel(AdministrationPanel a) {
        super(a);
    }

    @Override
    public void doShowPage() {
        super.doShowPage();

        adminPanel.getRegistryService().getLifecycles(new AbstractCallback<Collection<WLifecycle>>(adminPanel) {

            public void onSuccess(Collection<WLifecycle> lifecycles) {
                showLifecycles(lifecycles);
            }

        });


    }

    protected void showLifecycles(Collection<WLifecycle> lifecycles) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Lifecycles");
        cp.setBodyBorder(false);
        cp.setStyleName("x-panel-container-full");

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WLifecycle.class);

        List<BeanModel> list = factory.createModel(lifecycles);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);
        columns.add(new ColumnConfig("name", "Lifecycle Name", 200));

        ColumnConfig pcol = new ColumnConfig("phases", "Phases", 400);
        pcol.setRenderer(new ListCellRenderer(false));

        columns.add(pcol);
        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WUser s = store.getAt(ge.getRowIndex()).getBean();
                History.newItem("lifecycles/" + s.getId());
            }
        });

        cp.add(grid);

        // search filter
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store, BeanModel parent,
                                       BeanModel record, String property, String filter) {

                String name = record.get("name");
                name = name.toLowerCase();

                List p = record.get("phases");
                String phases = p.toString();
                phases = phases.toLowerCase();


                if (name.indexOf(filter.toLowerCase()) != -1 ||
                        phases.indexOf(filter.toLowerCase()) != -1) {
                    return true;
                }
                return false;
            }
        };

        filter.setName("Search");
        filter.setFieldLabel("Search");
        filter.setWidth(300);
        filter.setTriggerStyle("x-form-search-trigger");
        filter.setStyleName("x-form-search-field");
        // Bind the filter field to your grid store (grid.getStore())
        filter.bind(store);

        ToolBar toolbar = new ToolBar();
        toolbar.add(filter);
        toolbar.add(new FillToolItem());
        toolbar.add(createToolbarHistoryButton("New", "lifecycles/new"));
        cp.setTopComponent(toolbar);

        panel.add(cp);
    }


}
