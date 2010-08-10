package org.mule.galaxy.web.client.ui.field;

import org.mule.galaxy.web.client.ui.validator.FieldNotEmptyValidator;

import com.extjs.gxt.ui.client.widget.form.TextField;

public class RequiredTextField<D> extends TextField<D> {

    public RequiredTextField() {
        setValidateOnBlur(true);
        setAllowBlank(false);
        setValidator(new FieldNotEmptyValidator());
    }
}
