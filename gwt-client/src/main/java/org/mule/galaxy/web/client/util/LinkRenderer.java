/**
 *
 */
package org.mule.galaxy.web.client.util;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public final class LinkRenderer implements GridCellRenderer<BaseModel> {
    private final String anchor;


    public LinkRenderer(String anchor) {
        this.anchor = anchor;
    }

    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        String html = "";
        String value = (String) model.get(property);

        html += "<a href=\""+anchor+"\">" + value + "</a>";
        return html;
    }
}