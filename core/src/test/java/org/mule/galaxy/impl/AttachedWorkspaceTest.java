package org.mule.galaxy.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


import org.mule.galaxy.AttachedWorkspace;
import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class AttachedWorkspaceTest extends AbstractGalaxyTest {
    public void testAttached() throws Exception {
        Workspace parent = registry.getWorkspaces().iterator().next();
        
        AttachedWorkspace attached = registry.attachWorkspace(parent, 
                                                              "attached", 
                                                              "dummyWorkspaceManagerFactory",
                                                              new HashMap<String, String>());
        
        // is the attached workspace there?
        Collection<Workspace> workspaces = parent.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        assertEquals(attached, workspaces.iterator().next());

        DummyWorkspaceManager wm = (DummyWorkspaceManager) attached.getWorkspaceManager();
        
        assertEquals(attached, wm.getAttachedToWorkspace());
        assertEquals(parent, attached.getParent());
        
        assertNotNull(attached.getId());
        assertNotNull(attached.getCreated());
        assertNotNull(attached.getUpdated());
        assertEquals("attached", attached.getName());
        assertEquals(parent.getPath() + "attached/", attached.getPath());
        
        Collection<AttachedWorkspace> wkspcs = registry.getAttachedWorkspaces();
        assertEquals(1, wkspcs.size());
        
    }
}
