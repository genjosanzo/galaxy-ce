/**
 *
 */
package org.mule.galaxy.web.client.ui.renderer;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class BooleanCellRenderer implements GridCellRenderer<BaseModel> {

    /* prevents rendering "null" instead of an empty string in grids */
    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        final Boolean propertyValue = model.get(property);
        if (propertyValue != null && propertyValue) {
            return "Yes";
        }
        return "No";
    }


}