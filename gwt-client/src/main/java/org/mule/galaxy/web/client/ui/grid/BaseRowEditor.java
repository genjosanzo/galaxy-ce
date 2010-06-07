package org.mule.galaxy.web.client.ui.grid;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.event.RowEditorEvent;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;

public class BaseRowEditor<M extends ModelData> extends RowEditor<ModelData> {

    public static final EventType CANCEL_EDITING = new EventType();
    public static final EventType SAVE_EDITING = new EventType();

    @Override
    public void stopEditing(boolean saveChanges) {
        super.stopEditing(saveChanges);

        if (rendered && hidden) {

            RowEditorEvent ree = new RowEditorEvent(this, getRowIndex());
            BeanModel model = (BeanModel) getGrid().getStore().getModels().get(ree.getRowIndex());
            Record record = getRecord(model);
            ree.setRecord(record);

            if (saveChanges) {
                fireEvent(SAVE_EDITING, ree);
            } else {
                fireEvent(CANCEL_EDITING, ree);
            }
        }
    }

    private native Grid<BeanModel> getGrid()
        /*-{
           return this.@com.extjs.gxt.ui.client.widget.grid.RowEditor::grid;
        }-*/;

    private native int getRowIndex()
        /*-{
           return this.@com.extjs.gxt.ui.client.widget.grid.RowEditor::rowIndex;
        }-*/;
}