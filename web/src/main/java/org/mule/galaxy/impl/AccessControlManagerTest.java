package org.mule.galaxy.impl;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.UserUtils;

import java.util.List;
import java.util.Set;

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
    
    public void testAccess() throws Exception {
        Artifact artifact = importHelloWsdl();

        logout();
        
        try {
            accessControlManager.assertAccess(Permission.READ_ARTIFACT);
            fail("Expected Access Exception");
        } catch (AccessException e) {
            // expected
        }
        
        UserUtils.doPriveleged(new Runnable() {

            public void run() {
                try {
                    accessControlManager.assertAccess(Permission.READ_ARTIFACT);
                } catch (AccessException e) {
                    throw new RuntimeException(e);
                }
            }
            
        });
        
        login("admin", "admin");
        
        accessControlManager.assertAccess(Permission.READ_ARTIFACT);
        accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
        
        // try revoking permission to an artifact
        Group group = getGroup("Administrators", accessControlManager.getGroups());
        assertNotNull(group);
        accessControlManager.revoke(group, Permission.READ_ARTIFACT, artifact);

        accessControlManager.assertAccess(Permission.READ_ARTIFACT);
        try {
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
            fail("Expected Access Exception");
        } catch (AccessException e) {
            // expected
        }
        
        // clear the revocation and any grants
        accessControlManager.clear(group, artifact);
        accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
        
        User user = new User();
        user.setUsername("dan");
        user.setName("Dan");    
        userManager.create(user, "123");
        
        login("dan", "123");

        try {
            accessControlManager.assertAccess(Permission.READ_ARTIFACT);
            fail("Expected Access Exception");
        } catch (AccessException e) {
            // expected
        }

        try {
            accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
            fail("Expected Access Exception");
        } catch (AccessException e) {
            // expected
        }
    }
    
    private Group getGroup(String string, List<Group> groups) {
        for (Group group : groups) {
            if (string.equals(group.getName())) {
                return group;
            }
        }
        return null;
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] {
            "/META-INF/applicationContext-core.xml",
            "/META-INF/applicationContext-acegi-security.xml",
            "/META-INF/applicationContext-test.xml"
        };
    }

}