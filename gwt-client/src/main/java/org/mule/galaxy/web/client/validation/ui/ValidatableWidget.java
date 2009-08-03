package org.mule.galaxy.web.client.validation.ui;

import org.mule.galaxy.web.client.validation.Validator;

import com.google.gwt.user.client.ui.Widget;

/**
 * Allows you to attach validation messages to any widget you want.
 */
public class ValidatableWidget extends AbstractValidatableInputField {

    private final Widget inputWidget;

    public ValidatableWidget(Widget w, Validator validator) {
        super();
        this.inputWidget = w;
        init(validator);
    }

    @Override
    protected Widget createInputWidget() {
        return inputWidget;
    }

    @Override
    public boolean validate() {
        return getValidator().validate(inputWidget);
    }

}
