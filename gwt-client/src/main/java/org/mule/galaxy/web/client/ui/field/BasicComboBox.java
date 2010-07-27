package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.widget.form.ComboBox;

public class BasicComboBox<D> extends ComboBox {

    public BasicComboBox() {
        super();

        setTriggerAction(ComboBox.TriggerAction.ALL);
        setEditable(false);
        setAllowBlank(false);
        setForceSelection(true);
        setTypeAhead(true);

        addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
                onSelect(se.getSelectedItem());
            }
        });
    }


    protected void onSelect(BeanModel model) {
    }

}
