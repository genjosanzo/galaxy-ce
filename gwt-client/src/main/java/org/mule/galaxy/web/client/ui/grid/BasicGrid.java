package org.mule.galaxy.web.client.ui.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.tips.QuickTip;

public class BasicGrid<M extends ModelData> extends Grid {

    public BasicGrid(ListStore<M> store, ColumnModel cm) {
        super(store, cm);
        initDefaults();
    }

    public BasicGrid() {
        super();
        initDefaults();
    }

    private void initDefaults() {
        setBorders(true);
        setStripeRows(true);
        setAutoHeight(true);
        setAutoWidth(true);
    }

    public void enableQuickTip() {
        // for tooltips to work
        new QuickTip(this);
    }

}
