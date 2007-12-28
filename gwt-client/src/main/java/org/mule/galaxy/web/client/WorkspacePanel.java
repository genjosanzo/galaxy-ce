package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class WorkspacePanel
    extends Composite
{
    private RegistryPanel registryPanel;
    private VerticalPanel panel;
    private VerticalPanel artifactPanel;


    public WorkspacePanel(RegistryPanel rp) {
        super();
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        
        SearchPanel search = new SearchPanel(rp);
        panel.add(search);

        artifactPanel = new VerticalPanel();
        artifactPanel.setWidth("100%");
        panel.add(artifactPanel);

        registryPanel = rp;
        registryPanel.getRegistryService().getArtifacts(rp.getWorkspaceId(), 
                                                        rp.getArtifactTypes(), 
                                                        new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifacts((Collection) o);
            }
            
        });
        initWidget(panel);
    }
    
    protected void initArtifacts(Collection o) {
        for (Iterator groups = o.iterator(); groups.hasNext();) {
            ArtifactGroup group = (ArtifactGroup) groups.next();
            
            ArtifactListPanel list = new ArtifactListPanel(registryPanel, group);
            Label label = new Label(list.getTitle());
            label.setStyleName("right-title");
            artifactPanel.add(label);
            artifactPanel.add(list);
        }
    }
}
