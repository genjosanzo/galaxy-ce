package org.mule.galaxy.repository.client.admin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.repository.rpc.WArtifactType;
import org.mule.galaxy.web.client.admin.AbstractAdministrationComposite;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.util.FauxLinkRenderer;
import org.mule.galaxy.web.client.util.ListCellRenderer;
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

public class ArtifactTypeListPanel extends AbstractAdministrationComposite {

    private final RegistryServiceAsync registryService;

    public ArtifactTypeListPanel(AdministrationPanel a, RegistryServiceAsync registryService) {
        super(a);
        this.registryService = registryService;
    }

    @Override
    public void doShowPage() {
        super.doShowPage();

        registryService.getArtifactTypes(new AbstractCallback<Collection<WArtifactType>>(adminPanel) {

            public void onSuccess(Collection<WArtifactType> artifactTypes) {
                showArtifactTypes(artifactTypes);
            }

        });

    }

    protected void showArtifactTypes(Collection<WArtifactType> artifactTypes) {
        ContentPanel cp = new ContentPanel();
        cp.setHeading("Artifact Types");
        cp.setBodyBorder(false);
        cp.addStyleName("x-panel-container-full");
        cp.setAutoWidth(true);

        BeanModelFactory factory = BeanModelLookup.get().getFactory(WArtifactType.class);

        List<BeanModel> list = factory.createModel(artifactTypes);
        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(list);

        RowNumberer r = new RowNumberer();
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(r);

        ColumnConfig dConfig = new ColumnConfig("description", "Description", 200);
        dConfig.setRenderer(new FauxLinkRenderer());
        columns.add(dConfig);

        columns.add(new ColumnConfig("mediaType", "Media Types", 200));

        ColumnConfig docConfig = new ColumnConfig("documentTypes", "Document Type", 300);
        docConfig.setRenderer(new ListCellRenderer(true));
        columns.add(docConfig);

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.addPlugin(r);
        grid.setAutoWidth(true);
        grid.setAutoExpandColumn("documentTypes");
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                WArtifactType s = store.getAt(ge.getRowIndex()).getBean();

                History.newItem("artifact-types/" + s.getId());
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

                String mediaType = record.get("mediaType");
                mediaType = mediaType.toLowerCase();

                ArrayList dt = record.get("documentTypes");
                String documentTypes = dt.toString();
                documentTypes = documentTypes.toLowerCase();


                if (description.indexOf(filter.toLowerCase()) != -1 ||
                        mediaType.indexOf(filter.toLowerCase()) != -1 ||
                        documentTypes.indexOf(filter.toLowerCase()) != -1) {
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
        toolbar.add(createToolbarHistoryButton("New", "artifact-types/new"));
        cp.setTopComponent(toolbar);

        panel.add(cp);

    }
}
