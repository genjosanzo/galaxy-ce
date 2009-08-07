/*
 * $Id: LicenseHeader-GPLv2.txt 288 2008-08-25 00:59:35Z mark $
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

import org.mule.galaxy.web.client.util.FauxLinkRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WScriptJob;

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

/**
 * Show all scheduled items
 */
public class ScheduleListPanel extends AbstractAdministrationComposite {

    public ScheduleListPanel(AdministrationPanel a) {
        super(a);
    }


    @Override
    public void doShowPage() {
        super.doShowPage();

        adminPanel.getGalaxy().getAdminService().getScriptJobs(new AbstractCallback<List<WScriptJob>>(adminPanel) {
            public void onSuccess(List<WScriptJob> jobs) {
                showJobs(jobs);
            }

        });

    }


    private void showJobs(List<WScriptJob> jobs) {

        ContentPanel cp = new ContentPanel();
        cp.setHeading("Scheduled Jobs");
        cp.setBodyBorder(false);
        cp.setStyleName("x-panel-container-full");

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WScriptJob.class);

        List<BeanModel> list = factory.createModel(jobs);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);
        
        ColumnConfig nameConfig = new ColumnConfig("name", "Name", 150);
        nameConfig.setRenderer(new FauxLinkRenderer());
        columns.add(nameConfig);

        columns.add(new ColumnConfig("scriptName", "Script", 150));
        columns.add(new ColumnConfig("expression", "Cron Expression", 150));
        columns.add(new ColumnConfig("description", "Description", 250));
        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WScriptJob s = store.getAt(ge.getRowIndex()).getBean();
                History.newItem("schedules/" + s.getId());
            }
        });

        cp.add(grid);

        // search filter
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store, BeanModel parent,
                                       BeanModel record, String property, String filter) {

                String path = record.get("path");
                path = path.toLowerCase();

                String scriptName = record.get("scriptName");
                scriptName = scriptName.toLowerCase();

                String expression = record.get("expression");
                expression = expression.toLowerCase();

                String description = record.get("description");
                description = description.toLowerCase();

                if (path.indexOf(filter.toLowerCase()) != -1 ||
                        scriptName.indexOf(filter.toLowerCase()) != -1 ||
                        expression.indexOf(filter.toLowerCase()) != -1 ||
                        description.indexOf(filter.toLowerCase()) != -1) {
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
        toolbar.add(createToolbarHistoryButton("New", "schedules/new"));
        cp.setTopComponent(toolbar);

        panel.add(cp);

    }

}
