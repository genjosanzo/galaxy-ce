package org.mule.galaxy.web.client.item;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.mule.galaxy.web.client.AbstractFlowComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;

public class ChildItemsPanel extends AbstractFlowComposite {

    protected Collection items;
    private final RepositoryMenuPanel menuPanel;
    private final Galaxy galaxy;
    private final ItemInfo info;
    private CheckBoxSelectionModel<BeanModel> selectionModel;


    public ChildItemsPanel(Galaxy galaxy, RepositoryMenuPanel menuPanel, ItemInfo item) {
        super();
        this.galaxy = galaxy;
        this.menuPanel = menuPanel;
        this.info = item;
    }

    @Override
    public void doShowPage() {
        super.doShowPage();
        
        fetchAllItems();
    }


    private void fetchAllItems() {
        AbstractCallback callback = new AbstractCallback(menuPanel) {
            public void onSuccess(Object o) {
                items = (Collection) o;
                createItemGrid();
            }
        };

        galaxy.getRegistryService().getItems(info != null ? info.getId() : null, false, callback);
    }
    
    /**
     * generic grid for itemInfo
     *
     * @return
     */
    private void createItemGrid() {
        panel.clear();
        
        ContentPanel cp = new ContentPanel();
        cp.setStyleName("x-panel-container-full");
        cp.setBodyBorder(false);
        cp.setHeading(info != null ? info.getName()  : "All Items");
        cp.setAutoWidth(true);

        ToolBar toolbar = new ToolBar();
        // search filter
        StoreFilterField<BeanModel> filter = new StoreFilterField<BeanModel>() {
            @Override
            protected boolean doSelect(Store<BeanModel> store, BeanModel parent,
                                       BeanModel record, String property, String filter) {

                String name = record.get("name");
                name = name.toLowerCase();

                String authorName = record.get("authorName");
                authorName = authorName.toLowerCase();

                String type = record.get("type");
                type = type.toLowerCase();

                if (name.indexOf(filter.toLowerCase()) != -1 ||
                        authorName.indexOf(filter.toLowerCase()) != -1 ||
                        type.indexOf(filter.toLowerCase()) != -1) {
                    return true;
                }
                return false;
            }
        };

        toolbar.add(filter);
        toolbar.add(new FillToolItem());

        selectionModel = new CheckBoxSelectionModel<BeanModel>();

        BeanModelFactory factory = BeanModelLookup.get().getFactory(ItemInfo.class);
        List<BeanModel> model = factory.createModel(items);

        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(model);

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(selectionModel.getColumn());
        columns.add(new ColumnConfig("name", "Name", 150));
        columns.add(new ColumnConfig("authorName", "Author", 150));
        columns.add(new ColumnConfig("type", "Type", 100));

        ColumnModel cm = new ColumnModel(columns);

        Grid grid = new Grid<BeanModel>(store, cm);
        grid.setAutoWidth(true);
        grid.setSelectionModel(selectionModel);
        grid.setBorders(true);
        grid.addPlugin(selectionModel);
        grid.setAutoExpandColumn("name");

        grid.setAutoWidth(true);
        grid.addListener(Events.CellClick, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                GridEvent ge = (GridEvent) be;
                // any non checkbox...
                if (ge.getColIndex() > 0) {
                    ItemInfo ii = store.getAt(ge.getRowIndex()).getBean();

                    // drill down into the grid
                    menuPanel.showItem(ii);
                    History.newItem("item/" + ii.getId());
                }
            }
        });

        final Button delBtn = new Button("Delete");
        delBtn.setEnabled(false);
        delBtn.setEnabled(false);
        delBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                warnDelete(selectionModel.getSelectedItems());
            }
        });
        if (info == null || info.isDeletable()) {
            toolbar.add(delBtn);
        }

        selectionModel.addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
            public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
                boolean isSelected = selectionModel.getSelectedItems().size() > 0;
                delBtn.setEnabled(isSelected);
            }
        });


        cp.add(grid);

        filter.setName("Search");
        filter.setFieldLabel("Search");
        filter.setWidth(300);
        filter.setTriggerStyle("x-form-search-trigger");
        filter.setStyleName("x-form-search-field");
        filter.bind(store);


        if (info == null || info.isModifiable()) {
            String token;
            if (info != null) {
                token = "add-item/" + info.getId();
            } else {
                token = "add-item/";
            }
            toolbar.add(WidgetHelper.createSimpleHistoryButton("New", token));
        }
        cp.setTopComponent(toolbar);


        panel.add(cp);
    }


    protected void warnDelete(final List itemlist) {
        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    List<String> ids = new ArrayList<String>();
                    for (BeanModel data : selectionModel.getSelectedItems()) {
                        ids.add((String)data.get("id"));
                    }
                    
                    // FIXME: delete collection.
                    galaxy.getRegistryService().delete(ids, new AbstractCallback(menuPanel) {
                        public void onSuccess(Object arg0) {
                            menuPanel.setMessage("Items were deleted.");
                            fetchAllItems();
                        }
                    });
                    
                }
            }
        };
        MessageBox.confirm("Confirm", "Are you sure you want to delete these items?", l);
    }

}