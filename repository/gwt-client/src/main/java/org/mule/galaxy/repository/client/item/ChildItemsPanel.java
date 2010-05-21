package org.mule.galaxy.repository.client.item;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.ui.button.ToolbarButton;
import org.mule.galaxy.web.client.ui.button.ToolbarButtonEvent;
import org.mule.galaxy.web.client.ui.dialog.LightBox;
import org.mule.galaxy.web.client.ui.field.SearchStoreFilterField;
import org.mule.galaxy.web.client.ui.grid.BasicGrid;
import org.mule.galaxy.web.client.ui.panel.AbstractFlowComposite;
import org.mule.galaxy.web.client.ui.panel.FullContentPanel;
import org.mule.galaxy.web.client.ui.panel.InlineHelpPanel;
import org.mule.galaxy.web.client.ui.panel.PaddedContentPanel;
import org.mule.galaxy.web.client.ui.panel.WidgetHelper;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;
import com.extjs.gxt.ui.client.event.BaseEvent;
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
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.History;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ChildItemsPanel extends AbstractFlowComposite {

    protected Collection items;
    private final RepositoryMenuPanel menuPanel;
    private final Galaxy galaxy;
    private final ItemInfo info;
    private CheckBoxSelectionModel<BeanModel> selectionModel;
    private RepositoryModule repository;
    private ItemPanel itemPanel;

    public ChildItemsPanel(Galaxy galaxy, RepositoryMenuPanel menuPanel,
                           ItemInfo item, ItemPanel itemPanel) {
        super();
        this.galaxy = galaxy;
        this.repository = menuPanel.getRepositoryModule();
        this.menuPanel = menuPanel;
        this.info = item;
        this.itemPanel = itemPanel;
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

        repository.getRegistryService().getItems(info != null ? info.getId() : null, false, callback);
    }

    /**
     * generic grid for itemInfo
     *
     * @return
     */
    private void createItemGrid() {
        panel.clear();

        ContentPanel contentPanel = new FullContentPanel();
        contentPanel.setHeading("All Items");
        if (info != null) {
            contentPanel = new PaddedContentPanel();
            contentPanel.setHeading(info.getName());
            itemPanel.setHeading(info.getName());
        }
        contentPanel.setAutoHeight(true);

        // add inline help string and widget
        contentPanel.setTopComponent(new InlineHelpPanel(repository.getRepositoryConstants().repo_Tip(), 14));

        BeanModelFactory factory = BeanModelLookup.get().getFactory(ItemInfo.class);
        List<BeanModel> model = factory.createModel(items);

        final ListStore<BeanModel> store = new ListStore<BeanModel>();
        store.add(model);

        ToolBar toolbar = new ToolBar();
        // search filter
        SearchStoreFilterField<BeanModel> filter = new SearchStoreFilterField<BeanModel>() {
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

        filter.bind(store);

        toolbar.add(filter);
        toolbar.add(new FillToolItem());

        selectionModel = new CheckBoxSelectionModel<BeanModel>();

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(selectionModel.getColumn());
        columns.add(new ColumnConfig("name", "Name", 250));
        columns.add(new ColumnConfig("authorName", "Author", 200));
        columns.add(new ColumnConfig("type", "Type", 300));

        ColumnModel columnModel = new ColumnModel(columns);

        BasicGrid grid = new BasicGrid<BeanModel>(store, columnModel);
        grid.setSelectionModel(selectionModel);
        grid.addPlugin(selectionModel);
        grid.setAutoExpandColumn("type");
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

        final ToolbarButton delBtn = new ToolbarButton("Delete");
        delBtn.setToolTip(repository.getRepositoryConstants().repo_Delete());
        delBtn.setStyleName("toolbar-btn_left");
        delBtn.setEnabled(false);
        delBtn.addSelectionListener(new SelectionListener<ToolbarButtonEvent>() {
            @Override
            public void componentSelected(ToolbarButtonEvent ce) {
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

        contentPanel.add(toolbar);

        contentPanel.add(grid);

        if (info == null || info.isModifiable()) {
            if (info == null || info.getType().equals("Workspace")) {
                final ToolbarButton newWkspaceBtn = new ToolbarButton("New Workspace");
                newWkspaceBtn.setStyleName("toolbar-btn_center");
                newWkspaceBtn.addSelectionListener(new SelectionListener<ToolbarButtonEvent>() {
                    @Override
                    public void componentSelected(ToolbarButtonEvent ce) {
                        showNewWorkspace();
                    }
                });
                newWkspaceBtn.setToolTip(repository.getRepositoryConstants().repo_NewWorkspace());
                toolbar.add(newWkspaceBtn);

                final ToolbarButton newArtifactBtn = new ToolbarButton("New Artifact");
                newArtifactBtn.setStyleName("toolbar-btn_right");
                newArtifactBtn.addSelectionListener(new SelectionListener<ToolbarButtonEvent>() {
                    @Override
                    public void componentSelected(ToolbarButtonEvent ce) {
                        showArtifactUploadForm(true);
                    }
                });
                newArtifactBtn.setToolTip(repository.getRepositoryConstants().repo_NewArtifact());
                toolbar.add(newArtifactBtn);
            } else if (info.getType().equals("Artifact")) {
                final ToolbarButton newVersionBtn = new ToolbarButton("New Version");
                newVersionBtn.setStyleName("toolbar-btn_right");
                newVersionBtn.addSelectionListener(new SelectionListener<ToolbarButtonEvent>() {
                    @Override
                    public void componentSelected(ToolbarButtonEvent ce) {
                        showArtifactUploadForm(false);
                    }
                });

                newVersionBtn.setToolTip(repository.getRepositoryConstants().repo_Items_New());
                toolbar.add(newVersionBtn);
            } else {
                String token;
                if (info != null) {
                    token = "add-item/" + info.getId();
                } else {
                    token = "add-item/";
                }
                ToolbarButton newBtn = WidgetHelper.createToolbarHistoryButton("New", token,
                        "toolbar-btn_right", repository.getRepositoryConstants().repo_Items_New());
                toolbar.add(newBtn);
            }
        }

        panel.add(contentPanel);
    }


    protected void showArtifactUploadForm(boolean parentIsWorkspace) {
        LightBox popup = new LightBox(new AddArtifactForm(repository.getRegistryService(), info, parentIsWorkspace, this));
        popup.show();
    }

    protected void showNewWorkspace() {
        LightBox popup = new LightBox(new AddWorkspaceForm(repository.getRegistryService(), info));
        popup.show();
    }

    protected void warnDelete(final List itemlist) {
        final Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
            public void handleEvent(MessageBoxEvent ce) {
                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();

                if (Dialog.YES.equals(btn.getItemId())) {
                    final List<String> ids = new ArrayList<String>();
                    for (BeanModel data : selectionModel.getSelectedItems()) {
                        ids.add((String) data.get("id"));
                    }

                    // FIXME: delete collection.
                    repository.getRegistryService().delete(ids, new AbstractCallback(menuPanel) {
                        public void onSuccess(Object arg0) {
                            fetchAllItems();
                            menuPanel.removeItems(info, ids);
                            menuPanel.setMessage("Items were deleted.");
                        }
                    });

                }
            }
        };
        MessageBox.confirm("Confirm", "Are you sure you want to delete these items?", l);
    }

    public void refresh() {
        fetchAllItems();
        menuPanel.refresh();
        menuPanel.clearErrorMessage();
    }

}
