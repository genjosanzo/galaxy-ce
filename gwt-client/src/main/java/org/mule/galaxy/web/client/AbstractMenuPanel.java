package org.mule.galaxy.web.client;

import java.util.Collection;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.TreeListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class AbstractMenuPanel extends Composite {

    private DockPanel panel;
    private VerticalPanel leftMenu;
    private VerticalPanel mainPanel;
    private Widget mainWidget;
    private SimplePanel errorPanel;

    public AbstractMenuPanel() {
        super();
        
        panel = new DockPanel();
        
        leftMenu = new VerticalPanel();
        leftMenu.setStyleName("left-menu");
        
        panel.add(leftMenu, DockPanel.WEST);
        
        mainPanel = new VerticalPanel();
        mainPanel.setWidth("100%");
        mainPanel.setStyleName("main-panel");
        panel.add(mainPanel, DockPanel.CENTER);
        panel.setCellWidth(mainPanel, "100%");
        
        errorPanel = new SimplePanel();
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
}
