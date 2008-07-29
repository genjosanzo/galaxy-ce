package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.policy.PolicyException;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public interface Workspace extends Item {
    
    String getName();
    
    void setName(String name);
    
    Item getParent();
    
    Collection<Workspace> getWorkspaces();

    Workspace getWorkspace(String name);

    /**
     * Get the default lifecycle for this workspace. If there has been
     * no lifecycle explicitly set, it will use the parent's lifecycle.
     * If it gets to the top level workspace and there is no lifecycle
     * set, it will use the lifecycle from 
     * <code>LifecycleManager.getDefaultLifecycle()</code>.
     * 
     * @return
     */
    Lifecycle getDefaultLifecycle();
    
    void setDefaultLifecycle(Lifecycle l);
    
    String getPath();

    Calendar getCreated();
    
    Calendar getUpdated();
    
    LifecycleManager getLifecycleManager();
    
    CommentManager getCommentManager();    

    List<Item> getItems();
    
    /**
     * Creates an artifact from a Java representation of it (as opposed
     * to a byte[] level representation). The artifact must be apporved
     * by the appropriate policies, or an ArtifactPolicyException will be
     * throw.
     * 
     * @param workspace
     * @param data
     * @param versionLabel
     * @param user
     * @return
     * @throws RegistryException
     * @throws PolicyException
     * @throws MimeTypeParseException
     */
    EntryResult createArtifact(Object data, 
                               String versionLabel, 
                               User user) 
        throws DuplicateItemException, RegistryException, PolicyException, MimeTypeParseException, AccessException;
    
    EntryResult createArtifact(String contentType, 
                               String name,
                               String versionLabel, 
                               InputStream inputStream, 
                               User user) 
        throws DuplicateItemException, RegistryException, PolicyException, IOException, MimeTypeParseException, AccessException;

    EntryResult newEntry(String name, String versionLabel)
        throws DuplicateItemException, RegistryException, PolicyException, AccessException;
    
}
