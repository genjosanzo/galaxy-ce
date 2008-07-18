package org.mule.galaxy;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Set;

import org.mule.galaxy.lifecycle.Phase;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

import org.w3c.dom.Document;


public interface ArtifactVersion extends Item<Artifact> {
    
    String getId();
    
    Calendar getCreated();
    
    Calendar getUpdated();
    
    /**
     * Get the version label - i.e. "1.0".
     * @return
     */
    String getVersionLabel();
    
    /**
     * Get a Java API friendly representation of this document. This may be something
     * like a {@link Document} or a {@link Definition}.
     * @return
     */
    Object getData();
    
    InputStream getStream();

    Phase getPhase();
    
    ArtifactVersion getPrevious();
    
    /**
     * The author of this version. They may or may not be the actual author, but they
     * are the entity responsible for adding it to the repository.
     * @return
     */
    User getAuthor();
    
    Set<Link> getLinks();
    
    boolean isDefault();
    
    boolean isEnabled();
    
    void setEnabled(boolean enabled)
    	throws RegistryException, ArtifactPolicyException;
    
    boolean isIndexedPropertiesStale();

    /**
     * Sets the default version of an artifact to the specified one. It may
     * fail due to increased policy restrictions which have been enforced on 
     * the artifact.
     * 
     * @return
     * @throws RegistryException
     * @throws ArtifactPolicyException
     */
    void setAsDefaultVersion() throws RegistryException, ArtifactPolicyException;

}
