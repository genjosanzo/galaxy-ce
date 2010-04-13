package org.mule.galaxy.web.client.ui.validator;

import org.mule.galaxy.web.client.ui.field.Validator;

// TODO: remove this
public class StringNotEmptyValidator implements Validator {


    public boolean validate(Object value) {

        String s = (String) value;
        if(s == null ||  s.trim().equals("")) {
            return false;
        }
        return true;
    }


    public String getFailureMessage() {
        return "Field can not be empty";
    }
}
