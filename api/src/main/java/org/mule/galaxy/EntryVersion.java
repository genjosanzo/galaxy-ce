package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;

import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

import org.w3c.dom.Document;


public interface EntryVersion extends Item {
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    Entry getParent();
    
    /**
     * Get the version label - i.e. "1.0".
     * @return
     */
    String getVersionLabel();
    
    EntryVersion getPrevious();

    /**
     * The author of this version. They may or may not be the actual author, but they
     * are the entity responsible for adding it to the repository.
     * @return
     */
    User getAuthor();
    
    boolean isDefault();
    
    boolean isEnabled();
    
    void setEnabled(boolean enabled) throws RegistryException, PolicyException;
    
    boolean isIndexedPropertiesStale();

    /**
     * Sets the default version of an artifact to the specified one. It may
     * fail due to increased policy restrictions which have been enforced on 
     * the artifact.
     * 
     * @return
     * @throws RegistryException
     * @throws PolicyException
     */
    void setAsDefaultVersion() throws RegistryException, PolicyException;

}
