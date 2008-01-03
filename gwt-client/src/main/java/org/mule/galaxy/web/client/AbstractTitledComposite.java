package org.mule.galaxy.web.client;

import org.mule.galaxy.web.client.util.InlineFlowPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which puts a title up top with the "title" style.
 */
public abstract class AbstractTitledComposite extends AbstractComposite {

    protected Label title;
    
    protected void initWidget(Widget widget) {
        FlowPanel titlePanel = new FlowPanel();
        
        title = new Label();
        title.setStyleName("title");
        titlePanel.add(title);
        
        titlePanel.add(widget);
        
        super.initWidget(titlePanel);
    }

    public void setTitle(String titleText) {
        title.setText(titleText);
        
        super.setTitle(titleText);
    }

}
