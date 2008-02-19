package org.mule.galaxy;

import java.util.Calendar;
import java.util.Collection;

public interface Workspace {

    String getId();
    
    String getName();
    
    Workspace getParent();
    
    Collection<Workspace> getWorkspaces();

    Workspace getWorkspace(String name);

    String getPath();

    Calendar getCreated();
    
    Calendar getUpdated();
}
