package org.mule.galaxy.web.client.workspace;

import com.google.gwt.user.client.ui.TabPanel;

import java.util.Collection;

import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.admin.PolicyPanel;
import org.mule.galaxy.web.rpc.WWorkspace;

public class WorkspaceViewPanel extends AbstractComposite {

    public WorkspaceViewPanel(RegistryPanel registryPanel,
                              final Collection workspaces,
                              final String parentWorkspaceId,
                              final WWorkspace workspace) {
        super();

        TabPanel tabs = new TabPanel();

        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        tabs.add(new EditWorkspacePanel(registryPanel, workspaces, parentWorkspaceId, workspace), "Info");
        tabs.add(new PolicyPanel(registryPanel, registryPanel.getRegistryService(), workspace.getId()), "Governance");
        
        tabs.selectTab(0);
        
        initWidget(tabs);
    }

}
