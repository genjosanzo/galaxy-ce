package org.mule.galaxy.web.client;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractMenuPanel extends Composite implements ErrorPanel {

    private DockPanel panel;
    private FlowPanel leftMenuContainer;
    private FlowPanel mainPanel;
    private Widget mainWidget;
    private FlowPanel errorPanel;
    private FlowPanel leftMenu;
    
    public AbstractMenuPanel(Galaxy galaxy) {
        super();
        
        panel = new DockPanel();
        panel.setSpacing(0);
        
        leftMenu = new FlowPanel() {

            protected void onLoad() {

                Element br = DOM.createElement("br");
                DOM.setElementAttribute(br, "clear", "all");
                DOM.appendChild(DOM.getParent(leftMenu.getElement()), br);
            }
            
        };
        leftMenu.setStyleName("left-menu");
        
        panel.add(leftMenu, DockPanel.WEST);
        
        leftMenuContainer = new FlowPanel();
        leftMenuContainer.setStyleName("left-menu-container");
        
        Element br = DOM.createElement("br");
        DOM.setElementAttribute(br, "clear", "all");
        DOM.appendChild(leftMenuContainer.getElement(), br);

        
        leftMenu.add(leftMenuContainer);

        
        mainPanel = new FlowPanel();
        mainPanel.setStyleName("main-panel");
        panel.add(mainPanel, DockPanel.CENTER);
        panel.setCellWidth(mainPanel, "100%");
        

        br = DOM.createElement("br");
        DOM.setElementAttribute(br, "clear", "all");
        DOM.appendChild(DOM.getParent(mainPanel.getElement()), br);
        
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
        
        initWidget(panel);
    }


    public void addMenuItem(Widget widget) {
        leftMenuContainer.add(widget);
    }
    
    public void setMain(Widget widget) {
        if (mainWidget != null) {
            mainPanel.remove(mainWidget);
        }
        
        errorPanel.clear();
        mainPanel.remove(errorPanel);
        
        this.mainWidget = widget;
        
        mainPanel.add(widget);
        
    }
    
    protected int getErrorPanelPosition() {
        return 0;
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
}
