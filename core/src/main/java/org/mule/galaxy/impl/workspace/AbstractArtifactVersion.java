package org.mule.galaxy.impl.workspace;

import org.mule.galaxy.ArtifactVersion;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractArtifactVersion extends AbstractItem implements ArtifactVersion {
    private final WorkspaceManager workspaceManager;

    public AbstractArtifactVersion(WorkspaceManager workspaceManager) {
	super();
	
	this.workspaceManager = workspaceManager;
    }

    @Override
    public void delete() throws RegistryException, AccessException {
        workspaceManager.delete(this);
    }
}
