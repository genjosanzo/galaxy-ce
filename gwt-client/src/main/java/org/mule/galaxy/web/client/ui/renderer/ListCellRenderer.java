/**
 *
 */
package org.mule.galaxy.web.client.ui.renderer;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

import java.util.List;

public final class ListCellRenderer implements GridCellRenderer<BaseModel> {
    private final boolean newLine;

    public ListCellRenderer() {
        this(false);
    }

    public ListCellRenderer(boolean newLine) {
        this.newLine = newLine;
    }

    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        String html = "";
        List<Object> list = (List<Object>)model.get(property);
        if (list != null) {
            for (Object o : list) {
                if (newLine) {
                    html += "<div>" + o.toString() + "</div>";
                } else {
                    if (html.length() != 0) {
                        html += ", ";
                    }
                    html += o.toString();
                }
            }
        }
        return html;
    }
}