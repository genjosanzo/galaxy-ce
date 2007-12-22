package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;
import java.util.Iterator;

public class WorkspacePanel
    extends Composite
{
    private VerticalPanel panel;


    public WorkspacePanel(RegistryPanel registryPanel) {
        super();
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        
        SearchPanel search = new SearchPanel();
        panel.add(search);

        registryPanel.getRegistryService().getArtifacts(null, new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifacts((Collection) o);
            }
            
        });
        initWidget(panel);
    }
    protected void initArtifacts(Collection o) {
        for (Iterator groups = o.iterator(); groups.hasNext();) {
            ArtifactGroup group = (ArtifactGroup) groups.next();
            
            ArtifactListPanel list = new ArtifactListPanel(group);
            Label label = new Label(list.getTitle());
            label.setStyleName("right-title");
            panel.add(label);
            panel.add(list);
        }
    }
}
