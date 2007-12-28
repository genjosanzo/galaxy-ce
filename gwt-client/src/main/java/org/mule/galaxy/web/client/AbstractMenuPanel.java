package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class AbstractMenuPanel extends Composite {

    private DockPanel panel;
    private FlowPanel leftMenu;
    private FlowPanel mainPanel;
    private Widget mainWidget;
    private FlowPanel errorPanel;
    
    public AbstractMenuPanel(Galaxy galaxy) {
        super();
        
        panel = new DockPanel();
        panel.setSpacing(0);
        
        leftMenu = new FlowPanel();
        leftMenu.setStyleName("left-menu");
        panel.add(leftMenu, DockPanel.WEST);
        
        mainPanel = new FlowPanel();
        mainPanel.setWidth("100%");
        mainPanel.setStyleName("main-panel");
        panel.add(mainPanel, DockPanel.CENTER);
        panel.setCellWidth(mainPanel, "100%");
        
        errorPanel = new FlowPanel();
        errorPanel.setStyleName("error-panel");
        
        initWidget(panel);
    }


    public void addMenuItem(Widget widget) {
        leftMenu.add(widget);
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
    

    public void setMessage(Label label) {
        errorPanel.clear();
        errorPanel.add(label);
        mainPanel.insert(errorPanel, 0);
    }
    
    public void setMessage(String string) {
        setMessage(new Label(string));
    }
}
