package org.mule.galaxy.web.client.ui.validator;

import org.mule.galaxy.web.client.ui.field.Validator;
import org.mule.galaxy.web.client.ui.help.PanelConstants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ListBox;

public class ListBoxNotEmptyValidator implements Validator {

    private String msg;
    private final ListBox listBox;
    private static final PanelConstants panelMessages = (PanelConstants) GWT.create(PanelConstants.class);

    public ListBoxNotEmptyValidator(ListBox listBox) {
        super();
        this.listBox = listBox;
        this.msg = panelMessages.selectValue();
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
