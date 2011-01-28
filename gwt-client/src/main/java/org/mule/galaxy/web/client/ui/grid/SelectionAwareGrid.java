package org.mule.galaxy.web.client.ui.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.RowExpanderEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.widget.ComponentPlugin;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;

/**
 * 
 * {@link BasicGrid} keeping selected and expanded items after data refresh.
 * <br />
 * Underlying item type must provide implementation for {@link Object#hashCode()} and {@link Object#equals(Object)}.
 *
 * @param <M>
 */
public class SelectionAwareGrid<M extends ModelData> extends BasicGrid<M> {

    private final Set<M> selectedItems = new HashSet<M>();
    private final Set<M> expandedItems = new HashSet<M>();

    public SelectionAwareGrid(ListStore<M> store, ColumnModel cm) {
        super(store, cm);

        getStore().getLoader().addLoadListener(new LoadListener() {

            @Override
            public void loaderBeforeLoad(LoadEvent le) {
                saveSelectedItems();
            }

            @Override
            public void loaderLoad(LoadEvent le) {
                super.loaderLoad(le);

                restoreSelectedItems();
                restoreExpandedItems();
            }

        });
        //Do not keep in memory removed items
        getStore().addListener(ListStore.Remove, new Listener<StoreEvent<ModelData>>() {

            public void handleEvent(StoreEvent<ModelData> event) {
                List<ModelData> models = event.getModels();
                selectedItems.removeAll(models);
                expandedItems.removeAll(models);
            }
        });
    }

    @Override
    public void addPlugin(ComponentPlugin plugin) {
        super.addPlugin(plugin);

        if (plugin instanceof RowExpander) {
            trackExpandedItemsChanges((RowExpander) plugin);
        }
    }

    private void saveSelectedItems() {
        selectedItems.clear();
        selectedItems.addAll(getSelectionModel().getSelection());
    }

    private void trackExpandedItemsChanges(RowExpander rowExpander) {
        rowExpander.addListener(Events.BeforeExpand, new Listener<RowExpanderEvent>() {

            @SuppressWarnings("unchecked")
            public void handleEvent(RowExpanderEvent event) {
                expandedItems.add((M) event.getModel());
            }

        });

        rowExpander.addListener(Events.BeforeCollapse, new Listener<RowExpanderEvent>() {

            @SuppressWarnings("unchecked")
            public void handleEvent(RowExpanderEvent event) {
                expandedItems.remove((M) event.getModel());
            }

        });
    }

    private void restoreSelectedItems() {
        if (selectedItems.isEmpty()) {
            return;
        }

        if (!selectedItems.isEmpty()) {
            getSelectionModel().select(new ArrayList<M>(selectedItems), true);
        }
    }

    private void restoreExpandedItems() {
        for (M model : expandedItems) {
            int index = getView().findRowIndex(getView().getRow(model));
            if (index >= 0) {
                getRowExpanderIfAny().expandRow(index);
            }
        }
    }

    private RowExpander getRowExpanderIfAny() {
        for (ComponentPlugin plugin : getPlugins()) {
            if (plugin instanceof RowExpander) {
                return (RowExpander) plugin;
            }
        }
        return null;
    }

}