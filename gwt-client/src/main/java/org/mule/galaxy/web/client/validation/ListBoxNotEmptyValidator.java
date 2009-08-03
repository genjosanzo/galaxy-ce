package org.mule.galaxy.web.client.validation;

import com.google.gwt.user.client.ui.ListBox;

public class ListBoxNotEmptyValidator implements Validator {

    private String msg;
    private final ListBox listBox;

    public ListBoxNotEmptyValidator(ListBox listBox) {
        super();
        this.listBox = listBox;
        this.msg = "At least one value must be selected.";
    }

    public String getFailureMessage() {
        return msg;
    }

    public boolean validate(Object value) {
        return listBox.getItemCount() > 0;
    }

    public void setMessage(String msg) {
        this.msg = msg;
    }

}
