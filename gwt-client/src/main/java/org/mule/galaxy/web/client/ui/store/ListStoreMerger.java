package org.mule.galaxy.web.client.ui.store;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;

/**
 *
 * Base class implementing {@link ListStore} merging logic.
 *
 * @param <M>
 * @param <T>
 */
public abstract class ListStoreMerger<M extends BeanModel, T> {

    private final ListStore<M> store;

    public ListStoreMerger(final ListStore<M> store) {
        if (store == null) {
            throw new IllegalArgumentException("null store");
        }

        this.store = store;
    }

    public void merge(final Collection<T> objects) {
        final Map<String, T> filteredObjects = new HashMap<String, T>();
        for (final T object : objects) {
            if (!filter(object)) {
                final String id = extractIdentifier(object);
                final T previousValue = filteredObjects.put(id, object);
                if (previousValue != null) {
                    throw new IllegalStateException("Found several values for id <"+id+">");
                }
            }
        }
        final List<M> allAdded = new LinkedList<M>();
        final List<M> allUpdated = new LinkedList<M>();
        final List<M> allRemoved = new LinkedList<M>();
        for (final M model : this.store.getModels()) {
            final T newObject = filteredObjects.remove(extractIdentifier(model));
            if (newObject != null && isValid(newObject)) {
                final T oldObject = model.<T>getBean();
                if (hasBeenUpdated(oldObject, newObject)) {
                    final T updatedObject = update(oldObject, newObject);
                    final M updatedModel;
                    if (updatedObject == null) {
                        updatedModel = createModel(newObject);
                    } else {
                        updatedModel = model;
                    }
                    allUpdated.add(updatedModel);
                }
            } else {
                allRemoved.add(model);
            }
        }
        for (final T object : filteredObjects.values()) {
            if (isValid(object)) {
                allAdded.add(createModel(object));
            }
        }
        this.store.add(allAdded);
        for (final M updated : allUpdated) {
            this.store.update(updated);
        }
        for (final M removed : allRemoved) {
            this.store.remove(removed);
        }
        this.store.commitChanges();
    }

    protected abstract M createModel(final T object);

    /**
     * @param object
     * @return true to not consider specified object. Called first things once per object.
     */
    protected boolean filter(final T object) {
        return false;
    }

    /**
     * @param object
     * @return unique id for specified object. Must match unique model id.
     */
    protected abstract String extractIdentifier(final T object);

    /**
     * @param model
     * @return unique id for specified model. Must match unique object id.
     */
    protected abstract String extractIdentifier(final M model);

    /**
     * @param object
     * @return true if specified object should be store in {@link ListStore}. Usefull when an updated object must be removed from {@link ListStore}.
     */
    protected boolean isValid(final T object) {
        return true;
    }

    /**
     * @param oldBean
     * @param newBean
     * @return true if object has changed and must trigger a {@link ListStore#update(ModelData)} operation.
     */
    protected boolean hasBeenUpdated(final T oldBean, final T newBean) {
        return false;
    }

    /**
     * @param oldObject
     * @param newObject
     * @return null to create a complete new model from new object. Otherwise changes to oldObject will be applied.
     */
    protected T update(final T oldObject, final T newObject) {
        return null;
    }

}
