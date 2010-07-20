package org.mule.galaxy.web.client.ui.panel;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Layout;

/**
 * A ContentPanel with some sensible defaults so it actually shows up.
 */
public class BasicContentPanel extends ContentPanel {

    public BasicContentPanel() {
        super();
        setBodyBorder(false);
        setButtonAlign(Style.HorizontalAlignment.CENTER);
        setMonitorWindowResize(true);
        setAutoWidth(true);
        setAutoHeight(true);
    }

    public BasicContentPanel(Layout layout) {
        this();
        setLayout(layout);
    }

}
