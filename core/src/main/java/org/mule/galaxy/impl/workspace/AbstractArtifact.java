package org.mule.galaxy.impl.workspace;

import java.io.IOException;
import java.io.InputStream;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractArtifact extends AbstractEntry implements Artifact {

    public AbstractArtifact(WorkspaceManager manager, ItemMetadataHandler metadata) {
        super(manager, metadata);
    }

    public EntryResult newVersion(InputStream inputStream, String versionLabel, User user)
        throws RegistryException, PolicyException, IOException, DuplicateItemException,
        AccessException {
        return manager.newVersion(this, inputStream, versionLabel, user);
    }

    public EntryResult newVersion(Object data, String versionLabel, User user) throws RegistryException,
        PolicyException, IOException, DuplicateItemException, AccessException {
        return manager.newVersion(this, data, versionLabel, user);
    }

}
