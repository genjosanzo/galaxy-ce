package org.mule.galaxy.web.client.ui.store;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.GroupingStore;
import com.extjs.gxt.ui.client.store.ListStore;

public class ExtendedGroupingStore<M extends ModelData> extends GroupingStore<M> {

    public ExtendedGroupingStore() {
    }
    
    public ExtendedGroupingStore(ListLoader<?> loader) {
        super(loader);
    }
    
    /**
     * Returns the store's models.
     * <br />
     * Contrary to {@link ListStore#getModels()} this method also returns filtered items.
     * 
     * @return the items
     */
    public List<M> getAllModels() {
        if (isFiltered()) {
            return new ArrayList<M>(snapshot);
        } else {
            return getModels();
        }
    }
}
