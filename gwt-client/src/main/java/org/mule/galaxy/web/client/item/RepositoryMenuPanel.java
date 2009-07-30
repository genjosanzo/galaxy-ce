package org.mule.galaxy.web.client.item;

import org.mule.galaxy.web.client.AbstractShowable;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.MenuPanel;
import org.mule.galaxy.web.client.PageInfo;
import org.mule.galaxy.web.client.WidgetHelper;
import org.mule.galaxy.web.client.registry.ViewPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ItemInfo;

import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.data.TreeModelReader;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

import java.util.Collection;
import java.util.List;

public class RepositoryMenuPanel extends MenuPanel {

    private final Galaxy galaxy;
    private BaseTreeLoader<ModelData> loader;
    private TreeStore<ModelData> store;
    private TreePanel<ModelData> tree;
    private BaseTreeModel root;

    public RepositoryMenuPanel(Galaxy galaxy) {
        super();
        this.galaxy = galaxy;

        createPageInfo("browse", new ItemPanel(galaxy, this));
        createPageInfo("item/" + Galaxy.WILDCARD, new ItemPanel(galaxy, this));
        createPageInfo("add-item", new AddItemForm(galaxy, this));
        createPageInfo("view", new ViewPanel(galaxy));
    }

    @Override
    protected void onFirstShow() {
        super.onFirstShow();

        ContentPanel accordionPanel = WidgetHelper.createAccodionWrapperPanel();

        // browse panel
        ContentPanel browsePanel = new ContentPanel();
        browsePanel.setLayout(new FitLayout());
        browsePanel.setAutoHeight(true);
        browsePanel.setAutoWidth(true);
        browsePanel.setHeading("Browse");

        LayoutContainer treeContainer = new LayoutContainer();
        treeContainer.setStyleAttribute("backgroundColor", "white");
        treeContainer.addStyleName("no-border");

        browsePanel.add(treeContainer);
        // add to root
        accordionPanel.add(browsePanel);

        // search panel
        ContentPanel searchPanel = new ContentPanel();
        searchPanel.setLayout(new FitLayout());
        searchPanel.setAutoHeight(true);
        searchPanel.setAutoWidth(true);
        searchPanel.setHeading("Search");

        searchPanel.add(createSearchContainer());

        // add to root
        accordionPanel.add(searchPanel);


        loader = new BaseTreeLoader<ModelData>(new TreeModelReader<List<ModelData>>()) {
            @Override
            public boolean hasChildren(ModelData parent) {
                return true;
            }
        };

        store = new TreeStore<ModelData>(loader);
        store.setMonitorChanges(true);
        store.setKeyProvider(new ModelKeyProvider<ModelData>() {
            public String getKey(ModelData model) {
                return model.get("id");
            }
        });

        tree = new TreePanel<ModelData>(store);
        tree.setAutoSelect(true);
        tree.setAutoLoad(true);
        tree.setDisplayProperty("name");
        tree.setWidth(250);
        tree.setIconProvider(new ModelIconProvider<ModelData>() {
            public AbstractImagePrototype getIcon(ModelData model) {
                return null;
            }
        });

        // Ensure the groups are sorted alphabetically and that groups come before servers in the tree
        store.setStoreSorter(new StoreSorter<ModelData>() {
            @Override
            public int compare(Store<ModelData> store, ModelData m1, ModelData m2, String property) {
//                if ("all".equals(m1.get("id"))) {
//                    return -1;
//                }
//
//                if ("all".equals(m2.get("id"))) {
//                    return 1;
//                }
//
//
//                boolean isGroup1 = SERVER_GROUP_MODEL_TYPE.equals(m1.get("type"));
//                boolean isGroup2 = SERVER_GROUP_MODEL_TYPE.equals(m2.get("type"));
//
//                if (isGroup1 && !isGroup2) return -1;
//                if (isGroup2 && !isGroup1) return 1;
//
//                if (isGroup1 && isGroup2) {
//                    String name1 = m1.get("name");
//                    String name2 = m2.get("name");
//
//                    return name1.compareTo(name2);
//                }
                return 0;
            }

        });

        tree.getSelectionModel().addListener(Events.SelectionChange, new SelectionChangedListener<ModelData>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> data) {
                TreeModel selected = (TreeModel) data.getSelectedItem();
                if (selected == null) {
                    return;
                }

                String token = (String) selected.get("token");
                if (token != null) {
                    History.newItem(token);
                }
            }
        });

        root = new BaseTreeModel();

        loadItems();
        loader.load(root);


        tree.setAutoHeight(true);
        treeContainer.add(tree);
        tree.addListener(Events.Expand, new Listener<TreePanelEvent<ModelData>>() {
            public void handleEvent(TreePanelEvent<ModelData> be) {
                TreeModel parent = (TreeModel) be.getItem();
                loadItems(parent);
            }
        });


        // add accordion panel to left nav
        addMenuItem(accordionPanel);
    }


    private ContentPanel createSearchContainer() {
        ContentPanel cp = new ContentPanel();
        cp.setHeaderVisible(false);
        cp.setBodyBorder(false);
        cp.addStyleName("no-border");


        ButtonBar bb = new ButtonBar();
        Button search = new Button("Search");
        bb.add(search);

        TextField<String> name = new TextField<String>();
        name.setName("Name");

        cp.add(name);
        cp.setBottomComponent(bb);

        return cp;
    }

    private void loadItems() {
        loadItems(null);
    }

    protected void loadItems(final TreeModel parent) {
        String id = null;

        if (parent != null) {
            id = (String) parent.get("id");
            parent.removeAll();
        } else {
            store.removeAll();
        }

        galaxy.getRegistryService().getItems(id, new AbstractCallback<Collection<ItemInfo>>(this) {

            public void onSuccess(Collection<ItemInfo> items) {
                for (ItemInfo i : items) {
                    BaseTreeModel model = new BaseTreeModel();
                    model.set("id", i.getId());
                    model.set("name", i.getName());
                    model.set("token", "item/" + i.getId());
                    if (parent != null) {
                        store.add(parent, model, false);
                    } else {
                        store.add(model, false);
                    }
                }
            }

        });
    }

    public void createPageInfo(String token, final WidgetHelper composite) {
        PageInfo page = new PageInfo(token, getGalaxy().getRepositoryTab()) {

            public AbstractShowable createInstance() {
                return null;
            }

            public AbstractShowable getInstance() {
                RepositoryMenuPanel.this.setMain(composite);
                return RepositoryMenuPanel.this;
            }

        };
        getGalaxy().addPage(page);
    }

    public Galaxy getGalaxy() {
        return galaxy;
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
}
