package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WorkspacePanel
    extends Composite
{
    public WorkspacePanel() {
        super();
        
        VerticalPanel panel = new VerticalPanel();
        panel.setWidth("100%");
        
        SearchPanel search = new SearchPanel();
        panel.add(search);
        
        ArtifactListPanel list = new ArtifactListPanel();
        Label label = new Label(list.getTitle());
        label.setStyleName("right-title");
        panel.add(label);
        panel.add(list);
        
        initWidget(panel);
    }
}
