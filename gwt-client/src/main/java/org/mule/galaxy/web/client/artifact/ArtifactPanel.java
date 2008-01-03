package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;

/**
 * Contains:
 * - BasicArtifactInfo
 * - Service dependencies
 * - Depends on...
 * - Comments
 * - Governance tab
 *   (with history)
 * - View Artiact
 */
public class ArtifactPanel extends Composite {

    private RegistryPanel registryPanel;
    private TabPanel artifactTabs;
    private ExtendedArtifactInfo info;
    private ArtifactGroup group;
    private VerticalPanel panel;

    protected ArtifactPanel(RegistryPanel registryPanel) {
        this.registryPanel = registryPanel;
        panel = new VerticalPanel();
        panel.setWidth("100%");
        
        artifactTabs = new TabPanel();
        panel.add(artifactTabs);
        
        initWidget(panel);
    }
    
    private void init() {
        Label label = new Label(info.getValue(0));
        label.setStyleName("artifact-title");
        panel.insert(label, 0);
        
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Info");
        artifactTabs.selectTab(0);
        
        artifactTabs.add(new GovernancePanel(registryPanel, info), "Governance");
        artifactTabs.add(new HistoryPanel(registryPanel, info), "History");
    }

    public ArtifactPanel(RegistryPanel registryPanel, String artifactId) {
        this(registryPanel);
        
        registryPanel.getRegistryService().getArtifact(artifactId, new AbstractCallback(registryPanel) { 
            public void onSuccess(Object o) {
                group = (ArtifactGroup) o;
                info = (ExtendedArtifactInfo) group.getRows().get(0);
                
                init();
            }
        });
    }

}
