/**
 *
 */
package org.mule.galaxy.web.client.util;

import org.mule.galaxy.web.client.WidgetHelper;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public final class FauxLinkRenderer implements GridCellRenderer<BaseModel> {
    private final boolean hover;


    public FauxLinkRenderer(boolean hover) {
        this.hover = hover;
    }

    public FauxLinkRenderer() {
        this(true);
    }

    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        String value = (String) model.get(property);
        return WidgetHelper.createFauxLink(value, hover);
    }

}