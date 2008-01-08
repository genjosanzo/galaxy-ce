package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A Panel which puts a title up top with the "title" style.
 */
public abstract class AbstractTitledComposite extends AbstractComposite {

    protected SimplePanel title;
    
    protected void initWidget(Widget widget) {
        FlowPanel titlePanel = new FlowPanel();
        
        title = new SimplePanel();
        titlePanel.add(title);
        
        titlePanel.add(widget);
        
        super.initWidget(titlePanel);
    }

    public void setTitle(String titleText) {
        title.clear();
        title.add(createTitle(titleText));
        
        super.setTitle(titleText);
    }

}
