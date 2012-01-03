package org.mule.galaxy.web.client.ui.store;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * The store class encapsulates a client side cache of {@link ModelData} objects
 * which provide input data for Components such as the {@link ComboBox} and
 * {@link ListView ListView}. This extended one allows getting all stored models at once.
 *
 */
public class ExtendedListStore<M extends ModelData> extends ListStore<M> {

  /**
   * Creates a new store.
   * 
   */
    public ExtendedListStore() {
    }

    /**
     * Creates a new store.
     * 
     * @param loader the loader instance
     */
    public ExtendedListStore(ListLoader<?> loader) {
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
