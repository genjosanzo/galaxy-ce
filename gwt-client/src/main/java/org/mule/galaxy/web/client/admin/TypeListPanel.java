/*
 * $Id$
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

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.WType;
import org.mule.galaxy.web.client.WidgetHelper;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;

import java.util.ArrayList;
import java.util.List;

public class TypeListPanel
        extends AbstractAdministrationComposite {
    protected List<WType> types;

    public TypeListPanel(AdministrationPanel a) {
        super(a);
    }

    @Override
    public void doShowPage() {
        super.doShowPage();

        adminPanel.getRegistryService().getTypes(new AbstractCallback<List<WType>>(adminPanel) {

            public void onSuccess(List<WType> types) {
                //Collections.sort(types, new WTypeComparator());
                TypeListPanel.this.types = types;
                showTypes(types);
            }
        });
    }

    private void showTypes(List<WType> types) {

        ContentPanel cp = new ContentPanel();
        cp.setHeading("Types");
        cp.setBodyBorder(false);
        cp.setStyleName("x-panel-container-full");


        BeanModelFactory factory = BeanModelLookup.get().getFactory(WType.class);

        List<BeanModel> list = factory.createModel(types);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);

        ColumnConfig nameConfig = new ColumnConfig("name", "Name", 240);
        nameConfig.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ModelData> modelDataListStore, Grid<ModelData> modelDataGrid) {

                String propName = model.get(property);
                Boolean isSystem = model.get("system");

                if (propName == null || propName.trim().length() == 0) {
                    return "<empty>";
                }
                if (isSystem) {
                    return propName + " (Read Only)";
                }

                /*
                        String propName = type.getName();
                        if (propName == null || propName.trim().length() == 0) {
                            propName = "<empty>";
                        }

                        if (type.isSystem()) {
                            table.setText(i, 0, type.getName() + " (Read Only)");
                        } else {
                            Hyperlink hyperlink = new Hyperlink(propName,
                                                                "types/" + type.getId());

                            table.setWidget(i, 0, hyperlink);
                        }

                 */

                return WidgetHelper.createFauxLink(propName);
            }
        });
        columns.add(nameConfig);

        ColumnConfig mixinConfig = new ColumnConfig("mixinIds", "Mixins", 400);
        mixinConfig.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ModelData> modelDataListStore, Grid<ModelData> modelDataGrid) {
                ArrayList<String> mixinIds = model.get(property);
                StringBuilder mixins = new StringBuilder();
                if (mixinIds != null) {
                    boolean first = true;
                    for (String id : mixinIds) {
                        if (first) {
                            first = false;
                        } else {
                            mixins.append(", ");
                        }
                        mixins.append(getType(id).getName());
                    }
                }
                return mixins.toString();
            }
        });
        columns.add(mixinConfig);

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WType s = store.getAt(ge.getRowIndex()).getBean();
                // system styles are read only
                if (!s.isSystem()) {
                    History.newItem("types/" + s.getId());
                }
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

                ArrayList mixinIds = record.get("mixinIds");
                String ids = mixinIds.toString();
                ids = ids.toLowerCase();

                if (name.indexOf(filter.toLowerCase()) != -1 ||
                        ids.indexOf(filter.toLowerCase()) != -1) {
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
        toolbar.add(createToolbarHistoryButton("New", "types/new"));
        cp.setTopComponent(toolbar);

        panel.add(cp);

    }

    protected WType getType(String id) {
        for (WType t : types) {
            if (id.equals(t.getId())) return t;
        }
        return null;
    }
}
