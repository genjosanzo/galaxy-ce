package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;

public abstract class SearchStoreFilterField<M extends ModelData> extends StoreFilterField {

    public SearchStoreFilterField() {
        super();
        setName("Search");
        setFieldLabel("Search");
        setWidth(300);
        setTriggerStyle("x-form-search-trigger");
        addStyleName("x-form-search-field");
    }
}
