package org.mule.galaxy.web.client.validation;

import com.extjs.gxt.ui.client.widget.form.Field;

public class UrlValidator implements com.extjs.gxt.ui.client.widget.form.Validator {

    public String validate(Field<?> field, String value) {
        if (isValidUrl(value)) {
            return null;
        }
        return "The Url is malformed";
    }

    public static boolean isValidUrl(String value) {
        return value.matches("https?://([-\\w\\.]+)+(:\\d+)?(/([\\w/_\\.]*(\\?\\S+)?)?)?");
    }


}
