package org.mule.galaxy.web.client.workspace;

import java.util.Collection;

import com.google.gwt.user.client.ui.TabPanel;
import org.mule.galaxy.web.client.AbstractComposite;
import org.mule.galaxy.web.client.RegistryPanel;
import org.mule.galaxy.web.client.admin.PolicyPanel;

public class WorkspaceViewPanel extends AbstractComposite {

    public WorkspaceViewPanel(RegistryPanel registryPanel,
                              final Collection workspaces,
                              final String parentWorkspaceId,
                              final String workspaceId,
                              final String workspaceName) {
        super();

        TabPanel tabs = new TabPanel();

        tabs.setStyleName("artifactTabPanel");
        tabs.getDeckPanel().setStyleName("artifactTabDeckPanel");
        
        tabs.add(new EditWorkspacePanel(registryPanel, workspaces, parentWorkspaceId, workspaceId, workspaceName), "Info");
        tabs.add(new PolicyPanel(registryPanel, registryPanel.getRegistryService(), workspaceId), "Governance");
        
        tabs.selectTab(0);
        
        initWidget(tabs);
    }

}
