package org.mule.galaxy.web.client.workspace;

import com.google.gwt.user.client.ui.SourcesTabEvents;
import com.google.gwt.user.client.ui.TabListener;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.Collection;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.client.artifact.ItemGroupPermissionPanel;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WWorkspace;

public class ManageWorkspacePanel extends AbstractComposite {

    public ManageWorkspacePanel(RegistryPanel registryPanel,
                              final Collection workspaces,
                              final String parentWorkspaceId,
                              final WWorkspace workspace) {
        super();

        final TabPanel tabs = new TabPanel();

        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        tabs.add(new EditWorkspacePanel(registryPanel, workspaces, parentWorkspaceId, workspace), "Info");
        tabs.add(new PolicyPanel(registryPanel, registryPanel.getRegistryService(), workspace.getId()), "Governance");
//      if (registryPanel.getGalaxy().hasPermission("MANAGE_GROUPS")) {
        tabs.add(new ItemGroupPermissionPanel(registryPanel, workspace.getId(), SecurityService.WORKSPACE_PERMISSIONS), "Security");
//        }
        tabs.selectTab(0);
        
        tabs.addTabListener(new TabListener() {

            public boolean onBeforeTabSelected(SourcesTabEvents arg0, int arg1) {
                return true;
            }

            public void onTabSelected(SourcesTabEvents events, int tab) {
                AbstractComposite composite = (AbstractComposite) tabs.getWidget(tab);
                
                composite.onShow();
            }
            
        });
        
        initWidget(tabs);
    }

}
