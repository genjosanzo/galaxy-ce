package org.mule.galaxy.web.client.artifact;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.util.InlineFlowPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactVersionInfo;
import org.mule.galaxy.web.rpc.ExtendedArtifactInfo;
import org.mule.galaxy.web.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.rpc.TransitionResponse;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;
import java.util.Iterator;

public class HistoryPanel extends AbstractComposite {

    private RegistryPanel registryPanel;
    private RegistryServiceAsync registryService;
    private FlowPanel panel;
    private ExtendedArtifactInfo info;

    public HistoryPanel(RegistryPanel registryPanel,
                        ExtendedArtifactInfo info) {
        super();
        this.registryPanel = registryPanel;
        this.registryService = registryPanel.getRegistryService();
        this.info = info;
        
        panel = new FlowPanel();
        initWidget(panel);
        
        setTitle("Artifact History");
        initializePanel();
    }

    protected void initializePanel() {
        for (Iterator iterator = info.getVersions().iterator(); iterator.hasNext();) {
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
            
            if (!av.isDefault()) {
                links.add(new Label(" | "));
                
                Hyperlink rollbackLink = new Hyperlink("Set Default", "rollback-version");
                rollbackLink.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        setDefault(av.getId());
                    }
                    
                });
                links.add(rollbackLink);
            }
            
            links.add(new Label(" | "));
            
            if (!av.isEnabled()) {
                Hyperlink enableLink = new Hyperlink("Reenable", "reenable-version");
                enableLink.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        setEnabled(av.getId(), true);
                    }
                    
                });
                links.add(enableLink);
            } else {
                Hyperlink disableLink = new Hyperlink("Disable", "disable-version");
                disableLink.addClickListener(new ClickListener() {

                    public void onClick(Widget w) {
                        setEnabled(av.getId(), false);
                    }
                    
                });
                links.add(disableLink);
            }
            
            panel.add(avPanel);
        }
    }

    protected void setDefault(String versionId) {
        registryService.setDefault(versionId, new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                TransitionResponse tr = (TransitionResponse) o;
                
                if (tr.isSuccess()) {
                    registryPanel.setMain(new ArtifactPanel(registryPanel, info.getId(), 2));
                } else {
                    displayMessages(tr);
                }
            }

        });
    }

    protected void setEnabled(String versionId, boolean enabled) {
        registryService.setEnabled(versionId, enabled, new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                TransitionResponse tr = (TransitionResponse) o;
                
                if (tr == null || tr.isSuccess()) {
                    registryPanel.setMain(new ArtifactPanel(registryPanel, info.getId(), 2));
                } else {
                    displayMessages(tr);
                }
            }

        });
    }
    
    protected void displayMessages(TransitionResponse tr) {
        registryPanel.setMessage("Policies were not met!");
    }

    

}
