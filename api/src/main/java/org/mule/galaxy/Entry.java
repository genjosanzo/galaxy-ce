package org.mule.galaxy;

import java.util.Calendar;
import java.util.List;

import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.type.Type;

public interface Entry extends Item {
    
    String getPath();
    
    Workspace getParent();
    
    String getName();
    
    void setName(String name);
    
    String getDescription();
    
    Type getType();
    
    void setType(Type type);
    
    void setDescription(String description);
    
    List<? extends EntryVersion> getVersions();

    EntryVersion getVersion(String versionName);

    /**
     * Get the default version of this entry. If this hasn't been specifically set,
     * its the latest version of the entry.
     */
    EntryVersion getDefaultOrLastVersion();
    
    EntryResult newVersion(String versionLabel) 
    	throws RegistryException, PolicyException, DuplicateItemException, AccessException;

}
