package org.mule.galaxy.repository.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WIndex;
import org.mule.galaxy.web.client.admin.AbstractAdministrationComposite;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.renderer.FauxLinkRenderer;
import org.mule.galaxy.web.rpc.AbstractCallback;

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

public class IndexListPanel extends AbstractAdministrationComposite {

    private final RegistryServiceAsync registryService;

    public IndexListPanel(AdministrationPanel a, RegistryServiceAsync registryService) {
        super(a);
        this.registryService = registryService;
    }

    @Override
    public void doShowPage() {
        super.doShowPage();

        registryService.getIndexes(new AbstractCallback<Collection<WIndex>>(adminPanel) {

            public void onSuccess(Collection<WIndex> indexes) {
                showIndexes(indexes);
            }

        });

    }

    private void showIndexes(Collection<WIndex> indexes) {

        ContentPanel cp = new ContentPanel();
        cp.setHeading("Indexes");
        cp.setBodyBorder(false);
        cp.addStyleName("x-panel-container-full");
        cp.setAutoWidth(true);
        cp.setAutoHeight(true);

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WIndex.class);

        List<BeanModel> list = factory.createModel(indexes);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);

        ColumnConfig dConfig = new ColumnConfig("description", "Index", 300);
        dConfig.setRenderer(new FauxLinkRenderer());
        columns.add(dConfig);

        ColumnConfig typeConfig = new ColumnConfig("indexer", "Language", 300);
        typeConfig.setRenderer(new GridCellRenderer<ModelData>() {
            public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex, ListStore<ModelData> modelDataListStore, Grid<ModelData> modelDataGrid) {
                String val = model.get(property);
                if ("xpath".equalsIgnoreCase(val)) {
                    return "XPath";
                } else if ("xquery".equalsIgnoreCase(val)) {
                    return "XQuery";

                } else if ("groovy".equalsIgnoreCase(val)) {
                    return "Groovy";
                }
                return val;
            }
        });
        columns.add(typeConfig);
        columns.add(new ColumnConfig("resultType", "Query Type", 100));

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setStripeRows(true);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.setAutoHeight(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WIndex s = store.getAt(ge.getRowIndex()).getBean();

                // don't link on this type
                if (!("org.mule.galaxy.impl.index.GroovyIndexer".equalsIgnoreCase(s.getIndexer()))) {
                    History.newItem("indexes/" + s.getId());
                }
            }
        });

        cp.add(grid);

        // search filter
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store, BeanModel parent,
                                       BeanModel record, String property, String filter) {

                String description = record.get("description");
                description = description.toLowerCase();

                String resultType = record.get("resultType");
                resultType = resultType.toLowerCase();

                String indexer = record.get("indexer");
                indexer = indexer.toLowerCase();

                if (description.indexOf(filter.toLowerCase()) != -1 ||
                        resultType.indexOf(filter.toLowerCase()) != -1 ||
                        indexer.indexOf(filter.toLowerCase()) != -1) {
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
        toolbar.add(createToolbarHistoryButton("New", "indexes/new"));
        cp.setTopComponent(toolbar);

        panel.add(cp);

    }

}



