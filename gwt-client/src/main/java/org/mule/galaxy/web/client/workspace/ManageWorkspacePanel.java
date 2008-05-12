package org.mule.galaxy.web.client.workspace;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.AbstractErrorShowingComposite;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.artifact.ItemGroupPermissionPanel;
import org.mule.galaxy.web.client.registry.RegistryMenuPanel;
import org.mule.galaxy.web.rpc.AbstractCallback;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WWorkspace;

public class ManageWorkspacePanel extends AbstractErrorShowingComposite {

    private FlowPanel panel;
    private String workspaceId;
    private final Galaxy galaxy;
    private WWorkspace workspace;

    public ManageWorkspacePanel(final Galaxy galaxy) {
        super();
        this.galaxy = galaxy;
        
        RegistryMenuPanel menuPanel = new RegistryMenuPanel();
        
        panel = new FlowPanel();
        menuPanel.setMain(panel);
        
        initWidget(menuPanel);
    }

    public void onShow(List params) {
        panel.clear();
        panel.add(new Label("Loading..."));
        
        if (params.size() > 0) {
            workspaceId = (String) params.get(0);
        }
        
        galaxy.getRegistryService().getWorkspaces(new AbstractCallback(this) {
            public void onSuccess(Object workspaces) {
                loadWorkspaces((Collection) workspaces);
            }
        });
    }
    
    public void loadWorkspaces(Collection workspaces) {
        panel.clear();
        
        Object[] parentAndWkspc = getWorkspace(workspaceId, null, workspaces);
        WWorkspace parent = (WWorkspace) parentAndWkspc[0];
        String parentId = parent != null ? parent.getId() : null;
        workspace = (WWorkspace) parentAndWkspc[1];
        
        panel.add(createTitle("Manage Workspace " + workspace.getName()));
        
        final TabPanel tabs = new TabPanel();
        panel.add(tabs);
        
        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        tabs.add(new WorkspaceForm(galaxy, workspaces, workspace, parentId), "Info");
        tabs.add(new PolicyPanel(this, galaxy, workspaceId), "Governance");
        if (galaxy.hasPermission("MANAGE_GROUPS")) {
            tabs.add(new ItemGroupPermissionPanel(galaxy, 
                                                  this,
                                                  workspaceId,
                                                  SecurityService.WORKSPACE_PERMISSIONS), "Security");
        }
        
        tabs.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents events, int tab) {
                AbstractComposite composite = (AbstractComposite) tabs.getWidget(tab);
                
                composite.onShow();
            }
            
        });
        tabs.selectTab(0);
        AbstractComposite composite = (AbstractComposite) tabs.getWidget(0);
        composite.onShow(Collections.EMPTY_LIST);
    }
    
    private Object[] getWorkspace(String id, WWorkspace parent, Collection workspaces) {
        if (workspaces == null) {
            return null;
        }
        for (Iterator itr = workspaces.iterator(); itr.hasNext();) {
            WWorkspace w = (WWorkspace) itr.next();
            
            if (w.getId().equals(id)) {
                return new Object[] { parent, w };
            }
            
            return getWorkspace(id, w, w.getWorkspaces());
        }
        return null;
    }

}
