package org.mule.galaxy.web.client.ui.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.RowExpander;
import com.google.gwt.user.client.Event;

/**
 * {@link CheckBoxSelectionModel} which do not force selection when a row is expanded.
 * <br />
 * Only useful when used with a {@link RowExpander}.
 *
 * @param <M>
 */
public class RowExpanderAwareCheckBoxSelectionModel<M extends ModelData> extends CheckBoxSelectionModel<M> {

    @Override
    protected void handleMouseDown(GridEvent<M> e) {
        if (e.getEvent().getButton() == Event.BUTTON_LEFT && e.getTarget().getClassName().equals("x-grid3-row-expander")) {
            return;
        }

        super.handleMouseDown(e);
    }

}
