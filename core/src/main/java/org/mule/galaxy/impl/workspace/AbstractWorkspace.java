package org.mule.galaxy.impl.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.ArtifactPolicyException;
import org.mule.galaxy.ArtifactResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractWorkspace extends AbstractItem implements Workspace {

    private final WorkspaceManager workspaceManager;

    public AbstractWorkspace(WorkspaceManager workspaceManager) {
	super();
	
	this.workspaceManager = workspaceManager;
    }

    public String getPath() {
        return getParent() + getName() + "/";
    }
    
    public abstract String getName();
    
    
    @Override
    public void delete() throws RegistryException, AccessException {
        workspaceManager.delete(this);
    }

    public ArtifactResult createArtifact(Object data, String versionLabel, User user)
        throws DuplicateItemException, RegistryException, ArtifactPolicyException, MimeTypeParseException,
        AccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public ArtifactResult createArtifact(String contentType, String name, String versionLabel,
                                         InputStream inputStream, User user) throws DuplicateItemException,
        RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, AccessException {
        // TODO Auto-generated method stub
        return null;
    }

    public Workspace getWorkspace(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<Workspace> getWorkspaces() {
        // TODO Auto-generated method stub
        return null;
    }

}
