package org.mule.galaxy.web.client.ui.validator;

import org.mule.galaxy.web.client.ui.help.PanelConstants;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.GWT;

public class UrlValidator implements com.extjs.gxt.ui.client.widget.form.Validator {

	 private static final PanelConstants panelMessages = (PanelConstants) GWT.create(PanelConstants.class);
    public String validate(Field<?> field, String value) {
        if (isValidUrl(value)) {
            return null;
        }
        return panelMessages.urlMalformed();
    }

    public static boolean isValidUrl(String value) {
        return value.matches("https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.\\-]*(\\?\\S+)?)?)?");
    }


}
