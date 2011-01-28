/**
 *
 */
package org.mule.galaxy.web.client.ui.renderer;

import java.util.Arrays;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class IterableCellRenderer implements GridCellRenderer<BaseModel> {
    private final boolean newLine;

    public IterableCellRenderer() {
        this(false);
    }

    public IterableCellRenderer(final boolean newLine) {
        this.newLine = newLine;
    }

    @SuppressWarnings("unchecked")
    public Object render(BaseModel model, String property, ColumnData config, int rowIndex,
                         int colIndex, ListStore<BaseModel> store, Grid<BaseModel> grid) {
        String html = "";
        final Object propertyValue = model.get(property);
        final Iterable<Object> iterable;
        if (propertyValue instanceof Object[]) {
            iterable = Arrays.asList((Object[]) propertyValue);
        } else {
            iterable = (Iterable<Object>) propertyValue;
        }
        if (iterable != null) {
            for (final Object o : iterable) {
                if (newLine) {
                    html += "<div>" + renderObjectAsHTML(o) + "</div>";
                } else {
                    if (html.length() != 0) {
                        html += ", ";
                    }
                    html += renderObjectAsHTML(o);
                }
            }
        }
        return html;
    }

    protected String renderObjectAsHTML(final Object o) {
        return o.toString();
    }
}