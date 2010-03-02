package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;

public abstract class SearchStoreFilterField<M extends ModelData> extends StoreFilterField {

    public SearchStoreFilterField() {
        super();
        setName("Search");
        setFieldLabel("Search");
        setWidth(300);
        setTriggerStyle("x-form-search-trigger");
        addStyleName("x-form-search-field");

        addKeyListener(new KeyListener() {
            @Override
            public void componentKeyPress(ComponentEvent compEvent) {
                FieldEvent e = (FieldEvent) compEvent;
                if (e.getKeyCode() == KeyboardEvents.Escape.getEventCode()) {
                    SearchStoreFilterField.this.clear();
                }
            }
        });
    }


}
