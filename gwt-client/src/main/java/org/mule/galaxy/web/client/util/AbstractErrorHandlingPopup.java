package org.mule.galaxy.web.client.util;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.ErrorPanel;

public abstract class AbstractErrorHandlingPopup extends PopupPanel implements ErrorPanel {

    protected FlowPanel errorPanel;
    protected FormPanel fpanel;

    public AbstractErrorHandlingPopup() {
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
        
        fpanel = new FormPanel();
        fpanel.setFrame(true);
        fpanel.setEncoding(FormPanel.Encoding.MULTIPART);
        fpanel.setMethod(FormPanel.Method.POST);
        fpanel.setButtonAlign(Style.HorizontalAlignment.CENTER);
        fpanel.setWidth(350);
        fpanel.getElement().<FormElement>cast().setTarget("_blank");

        setWidget(fpanel);
    }

    public void clearErrorMessage() {
        //errorPanel.clearState();
        errorPanel.clear();
        fpanel.remove(errorPanel);
    }

    public void setMessage(Widget label) {
        //errorPanel.clearState();
        errorPanel.clear();
        addMessage(label);
    }

    public void setMessage(String string) {
        setMessage(new Label(string));
    }

    public void addMessage(String message) {
        addMessage(new Label(message));
    }

    public void addMessage(Widget message) {
        errorPanel.add(message);
        fpanel.insert(errorPanel, getErrorPanelPosition());
        fpanel.layout();
    }

    protected int getErrorPanelPosition() {
        return 0;
    }

    protected FlowPanel getErrorPanel() {
        return errorPanel;
    }


}