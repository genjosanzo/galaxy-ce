package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;

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
        reloadArtifacts();
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
    
    public void reloadArtifacts() {
        artifactPanel.clear();
        
        String workspaceId   = registryPanel.getWorkspaceId();
        Set    artifactTypes = registryPanel.getArtifactTypes();
        registryPanel.getRegistryService().getArtifacts(workspaceId, 
                                                        artifactTypes, 
                                                        new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifacts((Collection) o);
            }
            
        });
    }
}
