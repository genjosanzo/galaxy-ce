package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import org.mule.galaxy.web.client.AbstractComposite;

/**
 * A widget which is encapsulated in a FlowPanel (aka &lt;div&gt;).
 */
public class AbstractFlowComposite extends AbstractComposite {
    protected FlowPanel panel;

    public AbstractFlowComposite() {
        super();
        
        panel = new FlowPanel();
        
        initWidget(panel);
    }

    public void onShow() {
        panel.clear();
        
    }
}
