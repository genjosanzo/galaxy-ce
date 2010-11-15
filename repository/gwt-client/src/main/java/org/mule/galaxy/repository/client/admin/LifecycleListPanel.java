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

package org.mule.galaxy.repository.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WLifecycle;
import org.mule.galaxy.web.client.admin.AbstractAdministrationComposite;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.client.ui.renderer.FauxLinkRenderer;
import org.mule.galaxy.web.client.ui.renderer.IterableCellRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;

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

public class LifecycleListPanel extends AbstractAdministrationComposite {

    private final RegistryServiceAsync registryService;

    public LifecycleListPanel(AdministrationPanel a, RegistryServiceAsync registryService) {
        super(a);
        this.registryService = registryService;
    }

    @Override
    public void doShowPage() {
        super.doShowPage();

        registryService.getLifecycles(new AbstractCallback<Collection<WLifecycle>>(adminPanel) {

            public void onCallSuccess(Collection<WLifecycle> lifecycles) {
                showLifecycles(lifecycles);
            }

        });


    }

    protected void showLifecycles(Collection<WLifecycle> lifecycles) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Lifecycles");
        cp.setBodyBorder(false);
        cp.addStyleName("x-panel-container-full");
        cp.setAutoWidth(true);
        cp.setAutoHeight(true);

        // add inline help string and widget
        cp.setTopComponent(
                new InlineHelpPanel(adminPanel.getGalaxy().getAdministrationConstants().admin_Lifecycles_Tip(), 20));

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WLifecycle.class);

        List<BeanModel> list = factory.createModel(lifecycles);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);

        ColumnConfig nameConfig = new ColumnConfig("name", "Lifecycle Name", 200);
        nameConfig.setRenderer(new FauxLinkRenderer());
        columns.add(nameConfig);

        ColumnConfig pcol = new ColumnConfig("phases", "Phases", 400);
        pcol.setRenderer(new IterableCellRenderer(false));
        columns.add(pcol);

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setStripeRows(true);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.setAutoHeight(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WLifecycle s = store.getAt(ge.getRowIndex()).getBean();
                History.newItem("lifecycles/" + s.getId());
            }
        });

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
        filter.addStyleName("x-form-search-field");
        // Bind the filter field to your grid store (grid.getStore())
        filter.bind(store);

        ToolBar toolbar = new ToolBar();
        toolbar.add(filter);
        toolbar.add(new FillToolItem());

        String tooltip = adminPanel.getGalaxy().getAdministrationConstants().admin_Lifecycles_New();
        toolbar.add(createToolbarHistoryButton("New", "lifecycles/new", tooltip));

        cp.add(toolbar);
        cp.add(grid);


        panel.add(cp);
    }


}
