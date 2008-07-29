package org.mule.galaxy.impl.workspace;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractEntry extends AbstractItem implements Entry {

    public AbstractEntry(WorkspaceManager manager, ItemMetadataHandler metadata) {
        super(manager, metadata);
    }

    public EntryResult newVersion(String versionLabel) throws RegistryException, PolicyException,
        DuplicateItemException, AccessException {
        return manager.newVersion(this, versionLabel);
    }


}
