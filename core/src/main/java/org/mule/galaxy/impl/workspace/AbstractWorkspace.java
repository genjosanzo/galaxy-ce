package org.mule.galaxy.impl.workspace;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.EntryResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Item;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractWorkspace extends AbstractItem implements Workspace {

    public AbstractWorkspace(WorkspaceManager manager, ItemMetadataHandler metadata) {
        super(manager, metadata);
    }

    public String getPath() {
        return getParent() + getName() + "/";
    }

    public EntryResult createArtifact(Object data, String versionLabel, User user)
        throws DuplicateItemException, RegistryException, PolicyException, MimeTypeParseException,
        AccessException {
        return manager.createArtifact(this, data, versionLabel, user);
    }

    public EntryResult createArtifact(String contentType, String name, String versionLabel,
                                         InputStream inputStream, User user) throws DuplicateItemException,
        RegistryException, PolicyException, IOException, MimeTypeParseException, AccessException {
        return manager.createArtifact(this, contentType, name, versionLabel, inputStream, user);
    }

    public EntryResult newEntry(String name, String versionLabel) throws DuplicateItemException,
        RegistryException, PolicyException, AccessException {
        return manager.newEntry(this, name, versionLabel);
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
