package org.mule.galaxy.impl.jcr;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.mule.galaxy.Item;
import org.mule.galaxy.Registry;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.event.EventManager;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyManager;
import org.mule.galaxy.security.AccessControlManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.workspace.WorkspaceManager;
import org.springmodules.jcr.JcrTemplate;

public interface JcrWorkspaceManager extends WorkspaceManager {
    EventManager getEventManager();

    Registry getRegistry();

    JcrTemplate getTemplate();

    LifecycleManager getLifecycleManager();

    AccessControlManager getAccessControlManager();

    PolicyManager getPolicyManager();

    UserManager getUserManager();

    void setRegistry(Registry registry);

	Item build(Node node) throws RepositoryException, ItemNotFoundException, AccessException, RegistryException;
}
