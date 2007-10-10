package org.mule.galaxy;

import java.util.Collection;

public interface Workspace {

    String getId();
    
    String getName();
    
    Workspace getParent();
    
    Collection<Workspace> getWorkspaces();

}
