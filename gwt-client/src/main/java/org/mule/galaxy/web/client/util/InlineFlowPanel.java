package org.mule.galaxy.web.client.util;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Adds a style attribute of "display: inline" to every Widget added.
 * This makes the panel behave as a series of &lt;span&gt;s instead of
 * a series of &lt;div&gt;s.
 */
public class InlineFlowPanel extends FlowPanel {

    public void add(Widget w) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.add(w);
    }

    public void insert(Widget w, int beforeIndex) {
        DOM.setStyleAttribute(w.getElement(), "display", "inline");
        super.insert(w, beforeIndex);
    }

}
