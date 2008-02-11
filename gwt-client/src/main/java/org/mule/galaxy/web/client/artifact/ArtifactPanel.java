package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
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
public class ArtifactPanel extends AbstractComposite {

    private RegistryPanel registryPanel;
    private TabPanel artifactTabs;
    private ExtendedArtifactInfo info;
    private ArtifactGroup group;
    private VerticalPanel panel;
    private int selectedTab;

    protected ArtifactPanel(RegistryPanel registryPanel, int selectedTab) {
        this.registryPanel = registryPanel;
        this.selectedTab = selectedTab;
        
        panel = new VerticalPanel();
        panel.setWidth("100%");
        
        artifactTabs = new TabPanel();
        artifactTabs.setStyleName("artifactTabPanel");
        artifactTabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        panel.add(artifactTabs);
        
        initWidget(panel);
    }
    
    private void init() {
        InlineFlowPanel artifactTitle = new InlineFlowPanel();
        artifactTitle.add(new Label(info.getPath()));
        artifactTitle.setStyleName("artifact-title");
        
        Image img = new Image("images/feed-icon-14x14.png");
        img.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                Window.open(info.getArtifactFeedLink(), null, null);
            }
            
        });
        img.setStyleName("feed-icon");
        
        artifactTitle.add(img);
        
        panel.insert(artifactTitle, 0);
        
        artifactTabs.add(new ArtifactInfoPanel(registryPanel, group, info), "Info");
        artifactTabs.add(new GovernancePanel(registryPanel, info), "Governance");
        artifactTabs.add(new HistoryPanel(registryPanel, info), "History");
        
        if (selectedTab > -1) {
            artifactTabs.selectTab(selectedTab);
        } else {
            artifactTabs.selectTab(0);
        }
    }

    public ArtifactPanel(RegistryPanel registryPanel, String artifactId) {
        this(registryPanel, artifactId, -1);
    }
    
    public ArtifactPanel(RegistryPanel registryPanel, String artifactId, int selectedTab) {
        this(registryPanel, selectedTab);
        
        registryPanel.getRegistryService().getArtifact(artifactId, new AbstractCallback(registryPanel) { 
            public void onSuccess(Object o) {
                group = (ArtifactGroup) o;
                info = (ExtendedArtifactInfo) group.getRows().get(0);
                
                init();
            }
        });
    }

}
