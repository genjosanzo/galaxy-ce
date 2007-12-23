package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
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

    public ArtifactPanel(RegistryPanel registryPanel, 
                         ArtifactGroup group,
                         BasicArtifactInfo info) {
        super();
        this.registryPanel = registryPanel;
        
        TabPanel artifactTabs = new TabPanel();
        
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Info");
        artifactTabs.selectTab(0);
        
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Governance");
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "History");
        
        initWidget(artifactTabs);
    }
    
}
