package org.mule.galaxy.web.client.ui.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ColumnModelEvent;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.StoreEvent;
import com.extjs.gxt.ui.client.store.StoreListener;
import com.extjs.gxt.ui.client.widget.grid.GridView;

/**
 *
 * {@link GridView} handling batches of {@link Store#Update} events.
 *
 */
public class BatchAwareGridView extends GridView {

    @Override
    protected void initListeners() {
        listener = new StoreListener<ModelData>() {

            @Override
            public void handleEvent(StoreEvent<ModelData> e) {
              if (grid.isViewReady()) {
                super.handleEvent(e);
              }
            }

            @Override
            public void storeAdd(StoreEvent<ModelData> se) {
              onAdd(ds, se.getModels(), se.getIndex());
            }

            @Override
            public void storeBeforeDataChanged(StoreEvent<ModelData> se) {
              onBeforeDataChanged(se);
            }

            @Override
            public void storeClear(StoreEvent<ModelData> se) {
              onClear(se);
            }

            @Override
            public void storeDataChanged(StoreEvent<ModelData> se) {
              onDataChanged(se);
            }

            @Override
            public void storeFilter(StoreEvent<ModelData> se) {
              onDataChanged(se);
            }

            @Override
            public void storeRemove(StoreEvent<ModelData> se) {
              onRemove(ds, se.getModel(), se.getIndex(), false);
            }

            @Override
            public void storeUpdate(StoreEvent<ModelData> se) {
                for (ModelData modelData : se.getModels()) {
                    onUpdate(ds, modelData);
                }
            }

          };

          columnListener = new Listener<ColumnModelEvent>() {
            public void handleEvent(ColumnModelEvent e) {
              if (grid.isViewReady()) {
                EventType type = e.getType();
                if (type == Events.HiddenChange) {
                  onHiddenChange(cm, e.getColIndex(), e.isHidden());
                } else if (type == Events.HeaderChange) {
                  onHeaderChange(e.getColIndex(), e.getHeader());
                } else if (type == Events.WidthChange) {
                  onColumnWidthChange(e.getColIndex(), e.getWidth());
                }
              }
            }
          };
    }

}
