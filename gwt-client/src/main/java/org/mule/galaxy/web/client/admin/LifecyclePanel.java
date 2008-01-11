package org.mule.galaxy.web.client.admin;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import org.mule.galaxy.web.client.AbstractComposite;

public class LifecyclePanel
    extends AbstractComposite
{
    private AdministrationPanel adminPanel;

    public LifecyclePanel(AdministrationPanel a) {
        super();
        
        this.adminPanel = a;
        
        FlowPanel panel = new FlowPanel();
        panel.add(createTitle("Lifecycles"));
        panel.add(new Label("Coming soon..."));
        
        initWidget(panel);
    }
}
