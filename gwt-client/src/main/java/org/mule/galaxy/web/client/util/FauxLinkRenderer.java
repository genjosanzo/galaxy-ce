/**
 *
 */
package org.mule.galaxy.web.client.util;

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
        String html = "";
        String value = (String) model.get(property);

        html += " <div style=\"text-decoration: none; color: #016c96;\" ";
        if (hover) {
            html += " onmouseover=\"this.style.textDecoration = 'underline'\" onmouseout=\"this.style.textDecoration = 'none'\" ";
        }
        html += ">" + value + "</div>";

        return html;
    }

}