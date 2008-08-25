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
        
        testAttached(parent);
    }
    
    public void testAttachedWithNoParent() throws Exception {
        testAttached(null);
    }
    
    public void testAttached(Workspace parent) throws Exception {
        AttachedWorkspace attached = registry.attachWorkspace(parent, 
                                                              "attached", 
                                                              "dummyWorkspaceManagerFactory",
                                                              new HashMap<String, String>());
            
        // is the attached workspace there?
        Collection<Workspace> workspaces;
        if (parent != null) {
            workspaces = parent.getWorkspaces();
            
            assertEquals(1, workspaces.size());
            assertEquals(attached, workspaces.iterator().next());
        } else {
            workspaces = registry.getWorkspaces();
            
            assertEquals(2, workspaces.size());
        }

        DummyWorkspaceManager wm = (DummyWorkspaceManager) attached.getWorkspaceManager();
        
        assertEquals(attached, wm.getAttachedToWorkspace());
        assertEquals(parent, attached.getParent());
        
        assertNotNull(attached.getId());
        assertNotNull(attached.getCreated());
        assertNotNull(attached.getUpdated());
        assertEquals("attached", attached.getName());
        
        if (parent != null) {
            assertEquals(parent.getPath() + "attached/", attached.getPath());
        } else {
            assertEquals("/attached/", attached.getPath());
        }
        
        Collection<AttachedWorkspace> wkspcs = registry.getAttachedWorkspaces();
        assertEquals(1, wkspcs.size());
        
    }
}
