package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.AbstractCallback;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
import org.mule.galaxy.web.client.ExtendedArtifactInfo;
import org.mule.galaxy.web.client.RegistryPanel;

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

    protected ArtifactPanel(RegistryPanel registryPanel) {
        this.registryPanel = registryPanel;
        
        artifactTabs = new TabPanel();
        
        initWidget(artifactTabs);
    }
    
    private void init() {
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Info");
        artifactTabs.selectTab(0);
        
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Governance");
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "History");
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
