package org.mule.galaxy.web.client.ui.field;

import com.extjs.gxt.ui.client.widget.form.TextField;


public class RestrictedTextField extends TextField {

    public RestrictedTextField() {
    }

    @Override
    public void setMaxLength(int maxLength) {
        super.setMaxLength(maxLength);
        applyMaxLength();
    }

    @Override
    protected void afterRender() {
        super.afterRender();
        applyMaxLength();
    }

    private void applyMaxLength() {
        if (rendered) {
            getInputEl().setElementAttribute("maxlength", getMaxLength());
        }
    }
}