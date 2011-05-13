package org.mule.galaxy.web.client.ui.validator;

import org.mule.galaxy.web.client.ui.field.Validator;
import org.mule.galaxy.web.client.ui.help.PanelConstants;

import com.google.gwt.core.client.GWT;

// TODO: remove this
public class StringNotEmptyValidator implements Validator {

	private static final PanelConstants panelMessages = (PanelConstants) GWT.create(PanelConstants.class);

    public boolean validate(Object value) {

        String s = (String) value;
        if(s == null ||  s.trim().equals("")) {
            return false;
        }
        return true;
    }


    public String getFailureMessage() {
        return panelMessages.fieldNotEmpty();
    }
}
