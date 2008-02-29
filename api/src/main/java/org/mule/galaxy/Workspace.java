package org.mule.galaxy;

import java.util.Calendar;
import java.util.Collection;

import org.mule.galaxy.lifecycle.Lifecycle;

public interface Workspace {

    String getId();
    
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
}
