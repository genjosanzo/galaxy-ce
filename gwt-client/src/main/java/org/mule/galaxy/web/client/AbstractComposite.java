package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import org.mule.galaxy.web.client.util.InlineFlowPanel;

public abstract class AbstractComposite extends Composite {

    protected InlineFlowPanel createTitle(String title) {
        InlineFlowPanel titlePanel = new InlineFlowPanel();
        titlePanel.setStyleName("rightlinked-title-panel");
        
        Label label = new Label(title);
        label.setStyleName("rightlinked-title");
        titlePanel.add(label);
        return titlePanel;
    }

}
