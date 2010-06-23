package org.mule.galaxy.web.client.ui;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.dom.client.FormElement;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.ui.panel.ErrorPanel;

public abstract class AbstractErrorHandlingPopup extends PopupPanel implements ErrorPanel {

    protected FlowPanel errorPanel;
    protected FormPanel fpanel;
    protected ContentPanel wrapperPanel;


    public AbstractErrorHandlingPopup() {
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");

        wrapperPanel = new ContentPanel();
        wrapperPanel.setWidth(350);
        wrapperPanel.setHeaderVisible(false);

        fpanel = new FormPanel();
        fpanel.setHeaderVisible(false);
        fpanel.setEncoding(FormPanel.Encoding.MULTIPART);
        fpanel.setMethod(FormPanel.Method.POST);
        fpanel.setButtonAlign(Style.HorizontalAlignment.CENTER);
        fpanel.setWidth(350);
        fpanel.getElement().<FormElement>cast().setTarget("_blank");

        wrapperPanel.add(fpanel);
        setWidget(wrapperPanel);
    }

    public void clearErrorMessage() {
        errorPanel.clear();
        fpanel.remove(errorPanel);
    }

    public void setMessage(Widget label) {
        errorPanel.clear();
        addMessage(label);
    }

    public Widget setMessage(String message) {
        Label w = new Label(message);
        setMessage(w);
        return w;
    }

    public Widget addMessage(String message) {
        Label w = new Label(message);
        addMessage(w);
        return w;
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

    public void removeMessage(Widget message) {
        errorPanel.remove(message);
    }

}