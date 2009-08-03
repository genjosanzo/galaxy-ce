package org.mule.galaxy.atom.client;

import java.util.Map;

import org.mule.galaxy.Registry;
import org.mule.galaxy.artifact.ContentService;
import org.mule.galaxy.type.TypeManager;
import org.mule.galaxy.workspace.WorkspaceManager;
import org.mule.galaxy.workspace.WorkspaceManagerFactory;

public class AtomWorkspaceManagerFactory extends WorkspaceManagerFactory {

    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static String ID = "atomWorkspaceManagerFactory";
    
    private ContentService contentService;     
    private Registry registry;
    private TypeManager typeManager;
    
    @Override
    public WorkspaceManager createWorkspaceManager(Map<String, String> configuration) {
        AtomWorkspaceManager wm = new AtomWorkspaceManager();
        wm.setUrl(configuration.get(URL));
        wm.setUsername(configuration.get(USERNAME));
        wm.setPassword(configuration.get(PASSWORD));
        wm.setContentService(contentService);
        wm.setRegistry(registry);
        wm.setTypeManager(typeManager);
        wm.initialize();
        return wm;
    }

    @Override
    public String getName() {
        return "Galaxy Atom API";
    }

    public void setContentService(ContentService contentService) {
        this.contentService = contentService;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public void setTypeManager(TypeManager typeManager) {
        this.typeManager = typeManager;
    }

}
