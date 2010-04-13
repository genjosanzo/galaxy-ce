/**
 *
 */
package org.mule.galaxy.web.client.ui.renderer;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public final class LinkRenderer implements GridCellRenderer<BaseModel> {
    private final String anchor;
     private final boolean nostyle;


    public LinkRenderer(String anchor) {
        this(anchor, true);
    }

    public LinkRenderer(String anchor, boolean nostyle) {
        this.anchor = anchor;
        this.nostyle = nostyle;
    }

    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        String html = "";
        String value = (String) model.get(property);

        html += "<a href=\""+anchor+"\"";
        html += " style=\"text-decoration : none\" ";
        if(nostyle) {
            html += " onmouseover=\"this.style.textDecoration = 'underline'\" onmouseout=\"this.style.textDecoration = 'none'\" ";
        }
        html += ">" + value + "</a>";

        return html;
    }
}