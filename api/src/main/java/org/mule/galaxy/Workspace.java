package org.mule.galaxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Collection;

import javax.activation.MimeTypeParseException;

import org.mule.galaxy.collab.CommentManager;
import org.mule.galaxy.lifecycle.Lifecycle;
import org.mule.galaxy.lifecycle.LifecycleManager;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.User;

public interface Workspace extends Item<Workspace> {
    
    String getName();
    
    void setName(String name);
    
    Workspace getParent();
    
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
     * @throws ArtifactPolicyException
     * @throws MimeTypeParseException
     */
    ArtifactResult createArtifact(Object data, 
                                  String versionLabel, 
                                  User user) 
        throws DuplicateItemException, RegistryException, ArtifactPolicyException, MimeTypeParseException, AccessException;
    
    ArtifactResult createArtifact(String contentType, 
                                  String name,
                                  String versionLabel, 
                                  InputStream inputStream, 
                                  User user) 
        throws DuplicateItemException, RegistryException, ArtifactPolicyException, IOException, MimeTypeParseException, AccessException;
    
}
