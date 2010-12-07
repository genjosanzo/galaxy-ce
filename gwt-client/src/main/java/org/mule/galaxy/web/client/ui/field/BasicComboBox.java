package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class BasicComboBox<D extends ModelData> extends ComboBox<D> {

    public BasicComboBox() {
        super();

        setTriggerAction(ComboBox.TriggerAction.ALL);
        setEditable(false);
        setAllowBlank(false);
        setForceSelection(true);
        setTypeAhead(true);
        setWidth("170px");
        setTriggerAction(ComboBox.TriggerAction.ALL);

        addSelectionChangedListener(new SelectionChangedListener<D>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<D> se) {
                onSelect(se.getSelectedItem());
            }
        });

        addListener(Events.Expand, new Listener<FieldEvent>() {
            public void handleEvent(FieldEvent be) {
                sort(getDisplayField());
            }
        });

        // most use this by default
        setDisplayField("name");
    }


    protected void onSelect(D model) {
    }

    protected void sort(String value, Style.SortDir dir) {
        if(getStore() != null) {
            getStore().sort(value, dir);
        }

    }
    protected void sort(String value) {
        sort(value, Style.SortDir.ASC);
    }

}
