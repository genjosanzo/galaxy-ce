package org.mule.galaxy.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.AccessException;
import org.mule.galaxy.security.Group;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.PermissionGrant;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.SecurityUtils;

public class AccessControlManagerTest extends AbstractGalaxyTest {
    
    public void testDao() throws Exception {
        List<Group> groups = accessControlManager.getGroups();
        assertEquals(2, groups.size());
        
        Group group = getGroup("Administrators", groups);
        assertNotNull(group);
        
        Set<Permission> perms = accessControlManager.getGrantedPermissions(group);
        assertTrue(perms.size() > 0);
        
        group = getGroup("Administrators", groups);
        assertNotNull(group);
//        
//        Group groupByName = accessControlManager.getGroupByName("Anonymous");
//        assertNotNull(groupByName);
        
        Group groupByName = accessControlManager.getGroupByName("Administrators");
        assertNotNull(groupByName);
        
        User admin = getAdmin();
        assertNotNull(admin.getGroups());
        assertEquals(2, admin.getGroups().size());
        
        perms = accessControlManager.getGrantedPermissions(admin);
        assertTrue(perms.size() > 0);
        
        Set<PermissionGrant> pgs = accessControlManager.getPermissionGrants(group);
        assertEquals(perms.size(), pgs.size());
        
        Group g2 = accessControlManager.getGroup(group.getId());
        g2.setName("test");
        
        accessControlManager.save(g2);
        
        Group g3 = accessControlManager.getGroup(g2.getId());
        assertNotNull(g3);
        assertEquals("test", g3.getName());
    }
    
    
    public void testGroupUserDelete() throws Exception {
        List<Group> groups = accessControlManager.getGroups();
        assertEquals(2, groups.size());
        
        Group group = getGroup("Users", groups);
        assertNotNull(group);
        
        accessControlManager.deleteGroup(group.getId());
        
        User user = userManager.getByUsername("admin");
        
        assertNotNull(user);
    }
    
    public void testItemGrants() throws Exception {
        Artifact artifact = importHelloWsdl();
        
        List<Group> groups = accessControlManager.getGroups();
        assertEquals(2, groups.size());
        
        Group group = getGroup("Administrators", groups);
        assertNotNull(group);
        
        accessControlManager.revoke(group, Permission.DELETE_ARTIFACT, artifact);
        
        Set<PermissionGrant> pgs = accessControlManager.getPermissionGrants(group, artifact);
        
        for (PermissionGrant pg : pgs) {
            if (pg.getPermission().equals(Permission.DELETE_ARTIFACT)) {
                assertEquals(PermissionGrant.Grant.REVOKED, pg.getGrant());
            } else {
                assertEquals("Permission for " + pg.getPermission() + " should be inherited.",
                             PermissionGrant.Grant.INHERITED, pg.getGrant());
            }
        }
        
        accessControlManager.grant(group, Permission.DELETE_ARTIFACT, artifact);
        
        pgs = accessControlManager.getPermissionGrants(group, artifact);
        
        for (PermissionGrant pg : pgs) {
            if (pg.getPermission().equals(Permission.DELETE_ARTIFACT)) {
                assertEquals(PermissionGrant.Grant.GRANTED, pg.getGrant());
            } else {
                assertEquals("Permission for " + pg.getPermission() + " should be inherited.",
                             PermissionGrant.Grant.INHERITED, pg.getGrant());
            }
        }
    }
    
    public void testReadOnly() throws Exception {
        Group ro = new Group();
        ro.setName("ReadOnly");
        accessControlManager.save(ro);

        ro = accessControlManager.getGroupByName("ReadOnly");
        
        accessControlManager.grant(ro, Permission.READ_ARTIFACT);
        accessControlManager.grant(ro, Permission.READ_WORKSPACE);
        
        User user = new User();
        user.setUsername("guest");
        user.setEmail("guest@guest.com");
        Set<Group> groups = new HashSet<Group>();
        groups.add(ro);
        user.setGroups(groups);
        
        userManager.create(user, "guest");
        
        login("guest", "guest");
        
        try {
            importHelloWsdl();
            fail("Bad security!");
        } catch (AccessException e) {
        }
        

        try {
            ((Workspace) registry.getItemByPath("Default Workspace")).newEntry("test", "1.0");
            fail("Bad security!");
        } catch (AccessException e) {
        }
        

        login("admin", "admin");
        Artifact artifact = importHelloWsdl();
        
        login("guest", "guest");
        try {
            artifact.setProperty("test", "test");
            fail("Bad security!");
        } catch (AccessException e) {
        }
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
        
        SecurityUtils.doPriveleged(new Runnable() {

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
        accessControlManager.assertAccess(Permission.MANAGE_GROUPS);
        accessControlManager.assertAccess(Permission.READ_ARTIFACT, artifact);
        accessControlManager.assertAccess(Permission.MODIFY_ARTIFACT, artifact);

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
