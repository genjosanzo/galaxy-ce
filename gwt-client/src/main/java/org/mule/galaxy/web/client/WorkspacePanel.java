package org.mule.galaxy.web.client;

import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.ArtifactGroup;
import org.mule.galaxy.web.rpc.WSearchResults;

import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Iterator;
import java.util.Set;

public class WorkspacePanel
    extends Composite
{
    private RegistryPanel registryPanel;
    private FlowPanel panel;
    private FlowPanel artifactPanel;
    private SearchPanel searchPanel;
    private int resultStart = 0;
    // TODO make it a configurable parameter, maybe per-user?
    private int maxResults = 15;
    
    public WorkspacePanel(RegistryPanel rp) {
        super();
        
        panel = new FlowPanel();
        
        searchPanel = new SearchPanel(rp);
        searchPanel.setStyleName("search-panel");
        panel.insert(searchPanel, 0);

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
    
    protected void initArtifacts(WSearchResults o) {
        createNavigationPanel(o);
        for (Iterator groups = o.getResults().iterator(); groups.hasNext();) {
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
    
    private void createNavigationPanel(WSearchResults o) {
        Widget w = panel.getWidget(1);
        if (w.getStyleName().equals("activity-nav-panel")) {
            panel.remove(1);
        }

        long resultSize = o.getTotal();
        if (resultSize > maxResults || resultStart > 0) {
            FlowPanel activityNavPanel = new FlowPanel();
            activityNavPanel.setStyleName("activity-nav-panel");
            Hyperlink hl;
            
            if (resultSize > maxResults && resultStart < o.getTotal()) {
                hl = new Hyperlink("Next", "next");
                hl.setStyleName("activity-nav-next");
                hl.addClickListener(new ClickListener() {
    
                    public void onClick(Widget arg0) {
                        resultStart += maxResults;
                        
                        reloadArtifacts();
                    }
                    
                });
                activityNavPanel.add(hl);
            }
            
            if (resultStart > 0) {
                hl = new Hyperlink("Previous", "previous");
                hl.setStyleName("activity-nav-previous");
                hl.addClickListener(new ClickListener() {
    
                    public void onClick(Widget arg0) {
                        resultStart = resultStart - maxResults;
                        if (resultStart < 0) resultStart = 0;
                        
                        reloadArtifacts();
                    }
                    
                });
                activityNavPanel.add(hl);
            }

            SimplePanel spacer = new SimplePanel();
            spacer.add(new HTML("&nbsp;"));
            activityNavPanel.add(spacer);
            
            panel.insert(activityNavPanel, 1);
        }
    }

    public void reloadArtifacts() {
        artifactPanel.clear();
        
        String workspaceId   = registryPanel.getWorkspaceId();
        Set    artifactTypes = registryPanel.getArtifactTypes();
        Set    predicates    = searchPanel.getPredicates();
        String freeformQuery = searchPanel.getFreeformQuery();
        registryPanel.getRegistryService().getArtifacts(workspaceId, artifactTypes, 
                                                        predicates, freeformQuery, 
                                                        resultStart, maxResults,
                                                        new AbstractCallback(registryPanel) {

            public void onSuccess(Object o) {
                initArtifacts((WSearchResults) o);
            }
            public void onFailure(Throwable caught) {
                registryPanel.setMessage(caught.getMessage());
            }
        });
    }
}
