package org.mule.galaxy.web.client.ui.field;

import org.mule.galaxy.web.client.ui.help.AdministrationConstants;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.KeyboardEvents;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.google.gwt.core.client.GWT;

public abstract class SearchStoreFilterField<M extends ModelData> extends StoreFilterField<M> {

	private static final AdministrationConstants administrationMessages = (AdministrationConstants) GWT.create(AdministrationConstants.class);
	
    public SearchStoreFilterField() {
        super();
        setName(administrationMessages.search());
        setFieldLabel(administrationMessages.search());
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
