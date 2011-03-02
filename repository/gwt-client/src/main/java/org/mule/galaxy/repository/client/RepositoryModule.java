package org.mule.galaxy.repository.client;

import org.mule.galaxy.repository.client.activity.ActivityPanel;
import org.mule.galaxy.repository.client.item.ArtifactPanel;
import org.mule.galaxy.repository.client.item.ItemPanel;
import org.mule.galaxy.repository.client.item.RepositoryMenuPanel;
import org.mule.galaxy.repository.client.item.WorkspacePanel;
import org.mule.galaxy.repository.rpc.ItemInfo;
import org.mule.galaxy.repository.rpc.RegistryService;
import org.mule.galaxy.repository.rpc.RegistryServiceAsync;
import org.mule.galaxy.web.client.Galaxy;
import org.mule.galaxy.web.client.GalaxyModule;
import org.mule.galaxy.web.client.admin.AdministrationPanel;
import org.mule.galaxy.web.client.ui.NavMenuItem;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class RepositoryModule implements GalaxyModule {
    protected Galaxy galaxy;
    protected RepositoryConstants repositoryConstants;
    private RegistryServiceAsync registryService;
    private int repositoryTabIndex = 0;
    protected RepositoryMenuPanel repositoryMenuPanel;
    
    public void initialize(Galaxy galaxy) {
        this.galaxy = galaxy;
        
        if (repositoryConstants == null) {
            this.repositoryConstants = (RepositoryConstants) GWT.create(RepositoryConstants.class);
        }
        
        createService();
        loadRepositoryTab();
        addAdministrationMenuItems();
    }

    protected void addAdministrationMenuItems() {

        AdministrationPanel adminPanel = galaxy.getAdministrationPanel();
        if (galaxy.hasPermission("VIEW_ACTIVITY")) {
            adminPanel.addUtilityMenuItem(new NavMenuItem("Activity",
                                                          "ActivityPanel",
                                                          new ActivityPanel(adminPanel, galaxy, registryService)));
        }
    }
    
    protected RepositoryMenuPanel createRepositoryPanels() {
        repositoryMenuPanel = new RepositoryMenuPanel(this);
        repositoryMenuPanel.createPageInfo("browse", createWorkspacePanel(null, null));
        return repositoryMenuPanel;
    }

    public int getRepositoryTab() {
        return repositoryTabIndex;
    }

    protected void loadRepositoryTab() {
        if (galaxy.hasPermission("READ_ITEM")) {
            galaxy.getPageManager().createTab(repositoryTabIndex, "Repository", "browse", repositoryConstants.repo_TabTip());
        }
        createRepositoryPanels();
    }

    private void createService() {
        this.registryService = (RegistryServiceAsync) GWT.create(RegistryService.class);
        ServiceDefTarget target = (ServiceDefTarget) registryService;
        String baseUrl = GWT.getModuleBaseURL();
        target.setServiceEntryPoint(baseUrl + "../handler/registry.rpc");
    }

    public Galaxy getGalaxy() {
        return galaxy;
    }

    public RepositoryConstants getRepositoryConstants() {
        return repositoryConstants;
    }

    public RegistryServiceAsync getRegistryService() {
        return registryService;
    }

    public void setRepositoryTab(int repositoryTabIndex) {
        this.repositoryTabIndex = repositoryTabIndex;
    }

    public WorkspacePanel createWorkspacePanel(ItemInfo info,
                                               ItemPanel itemPanel) {
        return new WorkspacePanel(galaxy, repositoryMenuPanel, info, itemPanel);
    }

    public ArtifactPanel createArtifactPanel(ItemInfo info,
                                             ItemPanel itemPanel) {
        return new ArtifactPanel(galaxy, repositoryMenuPanel, info, itemPanel);
    }
    

}
