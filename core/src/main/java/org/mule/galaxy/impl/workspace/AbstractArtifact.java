package org.mule.galaxy.impl.workspace;

import java.io.IOException;
import java.io.InputStream;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractArtifact extends AbstractItem implements Artifact {
    private final WorkspaceManager workspaceManager;

    public AbstractArtifact(WorkspaceManager workspaceManager) {
	super();
	
	this.workspaceManager = workspaceManager;
    }

    public String getPath() {
        return getParent().getPath() + getName();
    }
    
    @Override
    public void delete() throws RegistryException, AccessException {
        workspaceManager.delete(this);
    }

    public ArtifactResult newVersion(InputStream inputStream, String versionLabel, User user)
        throws RegistryException, ArtifactPolicyException, IOException, DuplicateItemException,
        AccessException {
        return workspaceManager.newVersion(this, inputStream, versionLabel, user);
    }

    public ArtifactResult newVersion(Object data, String versionLabel, User user) throws RegistryException,
        ArtifactPolicyException, IOException, DuplicateItemException, AccessException {
        return workspaceManager.newVersion(this, data, versionLabel, user);
    }

}
