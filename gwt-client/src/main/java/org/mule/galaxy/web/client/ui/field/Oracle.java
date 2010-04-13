package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

/**
 * An Oracle-like widget which does auto suggesting based on the combo box.
 *
 * @author Dan
 */
public class Oracle {
    private boolean firstOracle = true;
    private ComboBox<ModelData> combo;

    protected Oracle() {
    }

    public Oracle(DataProxy proxy, String template) {
        initialize(proxy, template, new ComboBox<ModelData>());
    }

    protected void initialize(DataProxy proxy, String template, ComboBox<ModelData> combo) {
        this.combo = combo;

        // loader
        final BasePagingLoader loader = new BasePagingLoader(proxy);
        loader.setRemoteSort(false);
        loader.setSortField("name");
        loader.setSortDir(Style.SortDir.ASC);

        ListStore<ModelData> store = new ListStore<ModelData>(loader);
        store.setMonitorChanges(true);
        store.sort("name", Style.SortDir.ASC);

        combo.setWidth(250);
        combo.setDisplayField("name");
        combo.setItemSelector("div.search-item");
        combo.setTemplate(template);
        combo.setStore(store);
        combo.setAllowBlank(true);
        combo.setEmptyText("Start typing...");
        combo.setForceSelection(true);
        combo.setMinLength(0);
        combo.setMinChars(0);
        combo.setHideTrigger(true);
        combo.setPageSize(-1);

        combo.addListener(Events.Focus, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                if (firstOracle) {
                    loader.load();
                    firstOracle = false;
                }
            }
        });

        // reset, needed to maintain sorting and popup behavior
        combo.addListener(Events.Blur, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                firstOracle = true;
            }

        });
    }

    public ComboBox getComboBox() {
        return combo;
    }

}
