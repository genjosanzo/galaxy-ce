package org.mule.galaxy.web.client.ui.panel;

import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * A ContentPanel which will fill the screen.
 */
public class FullContentPanel extends BasicContentPanel {

    public FullContentPanel() {
        super();
        addStyleName("x-panel-container-full");
        setLayout(new FitLayout());
    }

    public FullContentPanel(Layout layout) {
        this();
        setLayout(layout);
    }
}
