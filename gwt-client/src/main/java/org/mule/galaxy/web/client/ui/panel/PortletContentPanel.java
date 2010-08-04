package org.mule.galaxy.web.client.ui.panel;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;

public class PortletContentPanel extends ContentPanel {


    public PortletContentPanel() {
        super();
        setBodyBorder(false);
        setBorders(false);
        setMonitorWindowResize(true);
        setAutoWidth(true);
        setAutoHeight(true);
        setHeaderVisible(false);
        addStyleName("lr-padded-panel");
    }

    public PortletContentPanel(Layout layout) {
        this();
        setLayout(layout);
    }

}
