package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

/**
 * Forms the basis for a page that can show error messages at the top.
 */
public class AbstractErrorShowingComposite 
    extends AbstractComposite implements ErrorPanel  {

    private FlowPanel errorPanel;
    private FlowPanel mainPanel;
    
    public AbstractErrorShowingComposite() {
        super();

        mainPanel = new FlowPanel();
        mainPanel.setStyleName("main-panel");
        
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
    }
    
    public void clearErrorMessage() {
        errorPanel.clear();
        mainPanel.remove(errorPanel);
    }

    public void setMessage(Label label) {
        errorPanel.clear();
        
        int pos = getErrorPanelPosition();
        if (pos > mainPanel.getWidgetCount()) pos = mainPanel.getWidgetCount();
        errorPanel.add(label);
        
        mainPanel.insert(errorPanel, pos);
    }
    
    public void setMessage(String string) {
        setMessage(new Label(string));
    }

    protected int getErrorPanelPosition() {
        return 0;
    }

    protected FlowPanel getErrorPanel() {
        return errorPanel;
    }

    protected FlowPanel getMainPanel() {
        return mainPanel;
    }

}
