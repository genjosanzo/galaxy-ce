package org.mule.galaxy.web.client.util.panel;

import com.extjs.gxt.ui.client.widget.layout.FitLayout;


/**
 * A ContentPanel which will fill the screen.
 */
public class FullContentPanel extends BasicContentPanel {

    public FullContentPanel() {
        super();
        addStyleName("x-cardPanel-container-full");
        setLayout(new FitLayout());
    }

}
