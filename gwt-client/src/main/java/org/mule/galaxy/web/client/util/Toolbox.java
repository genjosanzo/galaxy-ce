package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class Toolbox extends Composite {

    private FlowPanel panel;
    private FlowPanel header;
    private FlowPanel buttonPanel;
    private SimplePanel titleHolder;
    
    public Toolbox() {
        super();

        FlowPanel base = new FlowPanel();
        base.setStyleName("toolbox");
        
        header = new FlowPanel();
        header.setStyleName("toolbox-header");
        titleHolder = new SimplePanel();
        titleHolder.setStyleName("toolbox-title");
        header.add(titleHolder);
        base.add(header);
        
        buttonPanel = new FlowPanel();
        buttonPanel.setStyleName("toolbox-buttons");
        header.add(buttonPanel);
        
        SimplePanel body = new SimplePanel();
        body.setStyleName("toolbox-body");
        
        panel = new FlowPanel();
        panel.setStyleName("toolbox-items");
        body.add(panel);
        
        base.add(body);
        
        initWidget(base);
    }
    
    public void addButton(Widget button) {
        buttonPanel.add(button);
    }
    
    public void setTitle(String title) {
        titleHolder.clear();
        Label titleLbl = new Label(title);
        titleLbl.setStyleName("toolbox-title-label");
        titleHolder.add(titleLbl);
        super.setTitle(title);
    }

    public void add(Widget w) {
        panel.add(w);
    }
}
