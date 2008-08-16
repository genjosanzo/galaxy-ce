package org.mule.galaxy.impl.workspace;

import java.util.List;

import org.mule.galaxy.DuplicateItemException;
import org.mule.galaxy.Entry;
import org.mule.galaxy.EntryResult;
import org.mule.galaxy.EntryVersion;
import org.mule.galaxy.RegistryException;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.workspace.WorkspaceManager;

public abstract class AbstractEntryVersion extends AbstractItem implements EntryVersion {

    public AbstractEntryVersion(WorkspaceManager manager, ItemMetadataHandler metadata) {
        super(manager, metadata);
    }
    
    public void setEnabled(boolean enabled) throws RegistryException, PolicyException {
        manager.setEnabled(this, enabled);
    }

    public EntryVersion getPrevious() {
        List<? extends EntryVersion> versions = getParent().getVersions();
        
        int i = versions.indexOf(this);
        
        if (i > 0) {
            return versions.get(i-1);
        }
        return null;
    }

}
