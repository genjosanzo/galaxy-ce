package org.mule.galaxy.web.client.artifact;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;

public class HistoryPanel extends AbstractComposite {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private FlowPanel panel;
    private ExtendedArtifactInfo info;
    private FlowPanel messages;

    public HistoryPanel(RegistryPanel registryPanel,
                           ExtendedArtifactInfo info) {
        super();
        this.registryPanel = registryPanel;
        this.registryService = registryPanel.getRegistryService();
        this.info = info;
        
        panel = new FlowPanel();
        
        registryService.getArtifactVersions(info.getId(), new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initializePanel((Collection) o);
            } 
            
        });
        initWidget(panel);
        
        setTitle("Artifact History");
    }

    protected void initializePanel(Collection avs) {
        for (Iterator iterator = avs.iterator(); iterator.hasNext();) {
            final ArtifactVersionInfo av = (ArtifactVersionInfo)iterator.next();
            
            FlowPanel avPanel = new FlowPanel();
            avPanel.setStyleName("artifact-version-panel");
            
            Label title = new Label("Version " + av.getVersionLabel());
            title.setStyleName("artifact-version-title");
            avPanel.add(title);
            
            FlowPanel bottom = new FlowPanel();
            avPanel.add(bottom);
            bottom.setStyleName("artifact-version-bottom-panel");
            
            bottom.add(new Label("By " + av.getAuthorName() 
                + " (" + av.getAuthorUsername() + ") on " + av.getCreated()));
            
            InlineFlowPanel links = new InlineFlowPanel();
            bottom.add(links);
            
            Hyperlink viewLink = new Hyperlink("View", "view-version");
            viewLink.addClickListener(new ClickListener() {

                public void onClick(Widget arg0) {
                    Window.open(av.getLink(), null, null);
                }
                
            });
            links.add(viewLink);
            
            if (!av.isActive()) {
                links.add(new Label(" | "));
                
                Hyperlink rollbackLink = new Hyperlink("Set Active", "rollback-version");
                links.add(rollbackLink);
            }
            
            panel.add(avPanel);
        }
    }

    

}
