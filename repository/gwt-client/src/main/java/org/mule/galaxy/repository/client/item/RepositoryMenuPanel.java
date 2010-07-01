package org.mule.galaxy.repository.client.item;

import org.mule.galaxy.repository.client.RepositoryModule;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.PageManager;
import org.mule.galaxy.web.client.ui.button.ToolbarButton;
import org.mule.galaxy.web.client.ui.button.ToolbarButtonEvent;
import org.mule.galaxy.web.client.ui.panel.MenuPanel;
import org.mule.galaxy.web.client.ui.panel.ToolbarButtonBar;
import org.mule.galaxy.web.client.ui.panel.WidgetHelper;
import org.mule.galaxy.web.client.ui.util.Images;
import org.mule.galaxy.web.client.ui.util.UIUtil;
import org.mule.galaxy.web.rpc.AbstractCallback;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RepositoryMenuPanel extends MenuPanel {

    private final Galaxy galaxy;
    private TreeStore<ModelData> store;
    private TreePanel<ModelData> tree;
    private BaseTreeModel root;
    protected Map<String, ModelData> idToData = new HashMap<String, ModelData>();
    private String itemId;
    private final RepositoryModule repository;
    private RegistryServiceAsync registryService;

    private ToolbarButton addBtn;
    private ToolbarButton deleteBtn;
    private ToolbarButton renameBtn;

    public RepositoryMenuPanel(RepositoryModule repository) {
        super();
        this.repository = repository;
        this.registryService = repository.getRegistryService();
        this.galaxy = repository.getGalaxy();

        createPageInfo("item/" + PageManager.WILDCARD, new ItemPanel(this));
        createPageInfo("add-item", new AddItemForm(this));

        setId("repositoryTabBody");
    }

    @Override
    public void showPage(List<String> params) {
        String token = History.getToken();
        itemId = null;
        if (token.startsWith("browse") || token.startsWith("item")) {
            if (params.size() > 0) {
                itemId = params.get(0);

                AbstractCallback getItemCallback = new AbstractCallback<ItemInfo>(this) {
                    public void onCallSuccess(ItemInfo item) {
                        Widget main = getMain();
                        if (main instanceof ItemPanel) {
                            ((ItemPanel) main).initializeItem(item);
                        }
                    }
                };
                registryService.getItemInfo(itemId, true, getItemCallback);

                loadItems(itemId);
            } else {
                loadItems(null);
            }
        }

        super.showPage(params);
    }

    public void refresh() {
        loadItems(itemId);
    }

    private void loadItems(String itemId) {
        AbstractCallback getItemsCallback = new AbstractCallback<Collection<ItemInfo>>(this) {
            public void onCallSuccess(Collection<ItemInfo> items) {
                loadAndExpandItems(items);
            }
        };
        repository.getRegistryService().getItems(itemId, true, getItemsCallback);
    }

    protected void loadAndExpandItems(Collection<ItemInfo> items) {
        TreeModel parent = root;

        mergeItems(items, parent);
    }

    private void mergeItems(Collection<ItemInfo> items, TreeModel parent) {
        if (itemId != null && itemId.equals(parent.get("id"))) {
            tree.getSelectionModel().select(parent, false);
            GWT.log("1");
        }

        if (items == null) {
            return;
        }

        tree.setExpanded(parent, true);
        List<ItemInfo> found = new ArrayList<ItemInfo>();

        // merge and remove non existent items
        for (Iterator<ModelData> itr = parent.getChildren().iterator(); itr.hasNext();) {
            TreeModel data = (TreeModel) itr.next();
            ItemInfo item = getItem((String) data.get("id"), items);
            if (item == null) {
                itr.remove();
                parent.remove(data);
            } else {
                found.add(item);
                mergeItems(item.getItems(), (TreeModel) data);
            }
        }

        // Add in new items
        for (ItemInfo item : items) {

            if (!found.contains(item)) {
                BaseTreeModel model = toModel(item);

                parent.add(model);
                mergeItems(item.getItems(), (TreeModel) model);
            }
        }

    }


    private ItemInfo getItem(String id, Collection<ItemInfo> items) {
        for (ItemInfo itemInfo : items) {
            if (id.equals(itemInfo.getId())) {
                return itemInfo;
            }
        }
        return null;
    }

    public void showItem(ItemInfo item) {
        if (item == null) {
            loadItems(null);
        } else {
            loadItems(item.getId());
        }
    }


    @Override
    protected void onFirstShow() {
        super.onFirstShow();

        ContentPanel panel = new ContentPanel();
        panel.setCollapsible(false);
        panel.setHeaderVisible(false);
        panel.setLayout(new FitLayout());
        panel.addStyleName("tree-container");

        final BaseTreeLoader<ModelData> loader = new BaseTreeLoader<ModelData>(new TreeModelReader<List<ModelData>>()) {
            @Override
            public boolean hasChildren(ModelData parent) {
                String type = parent.<String>get("type");
                if (type != null) {
                    if (type.equals("Artifact") || type.equals("Workspace")) {
                        return true;
                    }
                }
                // nothing else can have children
                return false;
            }
        };

        store = new TreeStore<ModelData>(loader);
        store.setMonitorChanges(true);

        tree = new TreePanel<ModelData>(store);
        tree.addStyleName("left-menu-tree");
        tree.setDisplayProperty("name");
        tree.setAutoLoad(true);
        tree.setAutoSelect(true);
        tree.setIconProvider(new ModelIconProvider<ModelData>() {
            public AbstractImagePrototype getIcon(ModelData model) {
                // you are a leaf if you are not a workspace
                String name = (String) model.get("type");
                if (name != null && !name.equalsIgnoreCase("Workspace")) {
                    return IconHelper.createPath(Images.ICON_TEXT);
                }

                // else you are a node
                return null;
            }
        });

        // Ensure the groups are sorted alphabetically and that groups come before servers in the tree
        store.setStoreSorter(new StoreSorter<ModelData>() {
            @Override
            public int compare(Store<ModelData> store, ModelData m1, ModelData m2, String property) {
                String name1 = m1.get("name");
                String name2 = m2.get("name");

                return name1.compareTo(name2);
            }

        });

        tree.getSelectionModel().addListener(Events.SelectionChange, new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> data) {

                TreeModel selected = (TreeModel) data.getSelectedItem();
                if (selected == null) {
                    return;
                }

                // toggle the delete group button
                boolean isWorkspace = "Workspace".equals(selected.get("type"));
                deleteBtn.setEnabled(isWorkspace);
                renameBtn.setEnabled(isWorkspace);

                String token = (String) selected.get("token");
                if (token != null) {
                    History.newItem(token);
                }

            }
        });

        root = new BaseTreeModel();
        root.set("name", "All");
        root.set("token", "browse");

        loader.load();

        store.add(root, false);

        tree.addListener(Events.Expand, new Listener<TreePanelEvent<ModelData>>() {
            public void handleEvent(TreePanelEvent<ModelData> be) {
                final TreeModel parent = (TreeModel) be.getItem();

                String id = (String) parent.get("id");
                repository.getRegistryService().getItems(id, false, new AbstractCallback<Collection<ItemInfo>>(RepositoryMenuPanel.this) {
                    public void onCallSuccess(Collection<ItemInfo> items) {
                        mergeItems(items, parent);
                    }
                });
            }
        });


        ToolbarButtonBar actionBar = new ToolbarButtonBar();

        // Button actions
        SelectionListener<ToolbarButtonEvent> btnListener = new SelectionListener<ToolbarButtonEvent>() {
            public void componentSelected(ToolbarButtonEvent event) {

                ToolbarButton btn = event.getToolbarButton();
                final ModelData selectedItem = tree.getSelectionModel().getSelectedItem();
                final boolean itemSelected = selectedItem != null;
                final String gid;
                final String name;

                if (itemSelected) {
                    gid = (String) selectedItem.get("id");
                    name = (String) selectedItem.get("name");
                } else {
                    gid = null;
                    name = null;
                }

                if (btn == addBtn) {
                    final MessageBox box = MessageBox.prompt("New Workspace", "");
                    box.addCallback(new Listener<MessageBoxEvent>() {
                        public void handleEvent(MessageBoxEvent be) {

                            if (!UIUtil.validatePromptInput(be, "Workspace name is required")) {
                                return;
                            }
                            // TODO:
                            // all checks passed, save
                            //saveWorkspace(new ServerGroup(null, be.getValue()));
                        }
                    });
                } else if (btn == renameBtn) {
                    // only if they have something selected in the tree
                    if (itemSelected) {
                        final MessageBox box = MessageBox.prompt("Rename Workspace", "");
                        box.getTextBox().setValue(name);

                        box.addCallback(new Listener<MessageBoxEvent>() {
                            public void handleEvent(MessageBoxEvent be) {
                                if (!UIUtil.validatePromptInput(be, "Workspace name is required")) {
                                    return;
                                }
                                // TODO:
                                //renameWorkspace(selectedItem, be.getValue());
                            }
                        });
                    }
                } else if (btn == deleteBtn) {
                    // only if they have something selected in the tree
                    if (itemSelected) {

                        Listener<MessageBoxEvent> l = new Listener<MessageBoxEvent>() {
                            public void handleEvent(MessageBoxEvent ce) {
                                com.extjs.gxt.ui.client.widget.button.Button btn = ce.getButtonClicked();
                                if (Dialog.YES.equals(btn.getItemId())) {
                                    // TODO:
                                    //deleteWorkspace(gid);
                                }
                            }
                        };
                        MessageBox.confirm("Confirm", "Are you sure you want to delete this Workspace?", l);
                    }
                }

            }
        };

        actionBar.add(new FillToolItem());
        addBtn = new ToolbarButton("New Workspace", btnListener);
        addBtn.setToolTip(repository.getRepositoryConstants().repo_NewWorkspace());
        actionBar.add(addBtn);
        renameBtn = new ToolbarButton("Rename", btnListener);
        actionBar.add(renameBtn);
        deleteBtn = new ToolbarButton("Delete", btnListener);
        deleteBtn.setToolTip(repository.getRepositoryConstants().repo_Delete());
        actionBar.add(deleteBtn);

        panel.setTopComponent(actionBar);

        panel.add(tree);
        addMenuItem(panel);

        tree.getSelectionModel().select(root, false);
    }

    protected BaseTreeModel toModel(ItemInfo i) {
        BaseTreeModel model = new BaseTreeModel();
        model.set("id", i.getId());
        model.set("name", i.getName());
        model.set("type", i.getType());
        model.set("token", "item/" + i.getId());
        idToData.put(i.getId(), model);
        return model;
    }

    public void createPageInfo(String token, final WidgetHelper composite) {
        PageInfo page = new PageInfo(token, repository.getRepositoryTab()) {

            public Widget createInstance() {
                return null;
            }

            public Widget getInstance() {
                RepositoryMenuPanel.this.setMain(composite);
                return RepositoryMenuPanel.this;
            }

        };
        getGalaxy().getPageManager().addPage(page);
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public RepositoryModule getRepositoryModule() {
        return repository;
    }

    public TreeStore<ModelData> getStore() {
        return store;
    }

    public TreePanel<ModelData> getTree() {
        return tree;
    }

    public BaseTreeModel getRoot() {
        return root;
    }

    public void removeItems(ItemInfo info, List<String> ids) {
        if (info == null) {
            loadItems(null);
            return;
        }

        TreeModel data = (TreeModel) idToData.get(info.getId());

        for (ModelData child : data.getChildren()) {
            if (ids.contains(child.get("id"))) {
                store.remove(data, child);
            }
        }
    }
}
