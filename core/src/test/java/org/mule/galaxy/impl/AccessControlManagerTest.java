package org.mule.galaxy.impl;

import java.util.List;
import java.util.Set;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class AccessControlManagerTest extends AbstractGalaxyTest {
    public void testDao() throws Exception {
        List<Group> groups = accessControlManager.getGroups();
        assertEquals(2, groups.size());
        
        Group group = getGroup("Administrators", groups);
        assertNotNull(group);
        
        assertNotNull(group.getUserIds());
        assertTrue(group.getUserIds().contains(getAdmin().getId()));
        
        Set<Permission> perms = accessControlManager.getGlobalPermissions(group);
        assertTrue(perms.size() > 0);
        
        groups = accessControlManager.getGroups(getAdmin());
        assertEquals(2, groups.size());
        
        group = getGroup("Administrators", groups);
        assertNotNull(group);
        
        perms = accessControlManager.getGlobalPermissions(getAdmin());
        assertTrue(perms.size() > 0);
    }
    
    private Group getGroup(String string, List<Group> groups) {
        for (Group group : groups) {
            if (string.equals(group.getName())) {
                return group;
            }
        }
        return null;
    }

}
