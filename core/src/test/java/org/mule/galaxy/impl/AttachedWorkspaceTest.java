package org.mule.galaxy.impl;

import java.util.Collection;
import java.util.HashMap;

import org.mule.galaxy.AttachedItem;
import org.mule.galaxy.Item;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class AttachedWorkspaceTest extends AbstractGalaxyTest {
    public void testAttached() throws Exception {
        Item parent = registry.getItems().iterator().next();
        
        testAttached(parent);
    }
    
    public void testAttachedWithNoParent() throws Exception {
        testAttached(null);
    }
    
    public void testAttached(Item parent) throws Exception {
        AttachedItem attached = registry.attachItem(parent, "attached", "dummyWorkspaceManagerFactory",
                                                    new HashMap<String, String>());
            
        // is the attached workspace there?
        Collection<Item> workspaces;
        if (parent != null) {
            workspaces = parent.getItems();
            
            assertEquals(1, workspaces.size());
            assertEquals(attached, workspaces.iterator().next());
        } else {
            workspaces = registry.getItems();
            
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
            assertEquals(parent.getPath() + "/attached", attached.getPath());
        } else {
            assertEquals("/attached", attached.getPath());
        }
        
        Collection<AttachedItem> wkspcs = registry.getAttachedItems();
        assertEquals(1, wkspcs.size());
        
    }
}
