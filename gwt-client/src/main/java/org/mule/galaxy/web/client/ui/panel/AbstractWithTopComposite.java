package org.mule.galaxy.web.client.ui.panel;

import com.google.gwt.user.client.ui.FlowPanel;

public class AbstractWithTopComposite extends AbstractErrorShowingComposite {

    protected FlowPanel topPanel;
    protected FlowPanel currentTopPanel;

    
    public AbstractWithTopComposite() {
        super();
        topPanel = new FlowPanel();
        topPanel.setStyleName("top-panel");
        
        getMainPanel().add(topPanel);
    }


    protected void setTop(FlowPanel top) {
        int idx = topPanel.getWidgetIndex(top);
        if (idx != -1) {
            topPanel.remove(idx);
            topPanel.insert(top, 0);
        } else {
            topPanel.insert(top, 0);
        }
        
        this.currentTopPanel = top;
    }
}
