package org.mule.galaxy.web.client.ui.grid;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ExtendableRowEditor;
import com.extjs.gxt.ui.client.widget.grid.Grid;

/**
 *
 * Adds a 'Delete' button to existing buttons.
 *
 * @param <M>
 */
public class RowEditorWithDeleteSuport<M extends ModelData> extends ExtendableRowEditor<M> {

    private static final String DELETE_BUTTON_LABEL = "Delete";
    
    @Override
    protected Button[] createExtraButtons() {
        final Button deleteBtn = new Button(DELETE_BUTTON_LABEL, new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(final ButtonEvent event) {
                final Grid<M> grid = RowEditorWithDeleteSuport.this.grid;
                grid.getStore().remove(grid.getSelectionModel().getSelectedItem());
                stopEditing(false);
            }
        });
        deleteBtn.setMinWidth(getMinButtonWidth());
        
        return new Button[] {deleteBtn};
    }

}
