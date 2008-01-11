package org.mule.galaxy.web.client;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;

public class WorkspacePanel
    extends Composite
{
    private RegistryPanel registryPanel;
    private FlowPanel panel;
    private FlowPanel artifactPanel;
    private SearchPanel searchPanel;


    public WorkspacePanel(RegistryPanel rp) {
        super();
        
        panel = new FlowPanel();
        
        searchPanel = new SearchPanel(rp);
        searchPanel.setStyleName("search-panel");
        panel.add(searchPanel);


        SimplePanel artifactPanelBase = new SimplePanel();
        artifactPanelBase.setStyleName("artifact-panel-base");
        panel.add(artifactPanelBase);
        
        artifactPanel = new FlowPanel();
        artifactPanel.setStyleName("artifact-panel");
        artifactPanelBase.add(artifactPanel);

        registryPanel = rp;
        reloadArtifacts();
        initWidget(panel);
    }
    
    protected void initArtifacts(Collection o) {
        for (Iterator groups = o.iterator(); groups.hasNext();) {
            ArtifactGroup group = (ArtifactGroup) groups.next();
            
            ArtifactListPanel list = new ArtifactListPanel(registryPanel, group);
            
            SimplePanel rightTitlePanel = new SimplePanel();
            rightTitlePanel.setStyleName("right-title-panel");
            artifactPanel.add(rightTitlePanel);

            Label label = new Label(list.getTitle());
            label.setStyleName("right-title");
            rightTitlePanel.add(label);
            
            SimplePanel listContainer = new SimplePanel();
            listContainer.setStyleName("artifact-list-container");
            listContainer.add(list);
            
            artifactPanel.add(listContainer);
        }
    }
    
    public void reloadArtifacts() {
        artifactPanel.clear();
        
        String workspaceId   = registryPanel.getWorkspaceId();
        Set    artifactTypes = registryPanel.getArtifactTypes();
        Set    predicates    = searchPanel.getPredicates();
        String freeformQuery = searchPanel.getFreeformQuery();
        registryPanel.getRegistryService().getArtifacts(workspaceId, artifactTypes, predicates, freeformQuery,
                                                        new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifacts((Collection) o);
            }
            public void onFailure(Throwable caught) {
                registryPanel.setMessage(caught.getMessage());
            }
        });
    }
}
