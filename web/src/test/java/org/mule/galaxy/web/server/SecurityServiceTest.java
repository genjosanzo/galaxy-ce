package org.mule.galaxy.web.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.mule.galaxy.Item;
import org.mule.galaxy.security.Permission;
import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.rpc.SecurityService;
import org.mule.galaxy.web.rpc.WGroup;
import org.mule.galaxy.web.rpc.WPermissionGrant;
import org.mule.galaxy.web.rpc.WUser;

public class SecurityServiceTest extends AbstractGalaxyTest {
    protected SecurityService gwtSecurityService;
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-core-extensions.xml", 
                              "/META-INF/applicationContext-spring-security.xml",
                              "/META-INF/applicationContext-web.xml",
                              "classpath*:/META-INF/galaxy-applicationContext.xml",
                              "/META-INF/applicationContext-test.xml" };
        
    }
    
    public void testSetProperty() throws Exception {
        gwtSecurityService.setUserProperty("test", "value");
        
        User admin = getAdmin();
        assertEquals("value", admin.getProperties().get("test"));
    }

    
    public void testUsers() throws Exception {
        Collection<WUser> users = gwtSecurityService.getUsers();
        
        assertEquals(1, users.size());
        
        WUser user = new WUser();
        user.setUsername("dandiep");
        user.setName("Dan Diephouse");
        String id = gwtSecurityService.addUser(user, "foo");
        
        assertNotNull(id);
    }

    public void testPermissions() throws Exception {
        Map group2Perm = gwtSecurityService.getGroupPermissions();
        
        assertEquals(1, group2Perm.size());
        
        WGroup g = null;
        Collection permGrants = null;
        for (Iterator itr = group2Perm.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            g = (WGroup) e.getKey();
            permGrants = (Collection) e.getValue();
            if (g.getName().equals("Administrators")) break;
        }
        
        assertNotNull(g.getId());
        assertNotNull(g.getName());
        
        assertTrue(permGrants.size() > 0);
        
        WPermissionGrant pg = (WPermissionGrant) permGrants.iterator().next();
        assertNotNull(pg.getPermission());
        assertEquals(WPermissionGrant.GRANTED, pg.getGrant());
    }
    

    public void testItemPermissions() throws Exception {
        Item artifact = importHelloWsdl();
        Map group2Perm = gwtSecurityService.getGroupPermissions(artifact.getId());
        
        assertEquals(1, group2Perm.size());
        
        WGroup g = null;
        Collection permGrants = null;
        for (Iterator itr = group2Perm.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            g = (WGroup) e.getKey();
            permGrants = (Collection) e.getValue();
            if (g.getName().equals("Administrators")) break;
        }
        
        assertNotNull(g.getId());
        assertNotNull(g.getName());
        
        assertEquals(4, permGrants.size());
        
        WPermissionGrant pg = (WPermissionGrant) permGrants.iterator().next();
        assertNotNull(pg.getPermission());
        assertEquals(WPermissionGrant.INHERITED, pg.getGrant());
        
        /* Revoke all the artifactpermissions and test things again.
         */
        ArrayList<String> toRevoke = new ArrayList<String>();
        toRevoke.add(Permission.DELETE_ITEM);
        accessControlManager.revoke(accessControlManager.getGroup(g.getId()),
                                    toRevoke,
                                    artifact);
        
        group2Perm = gwtSecurityService.getGroupPermissions(artifact.getId());
        for (Iterator itr = group2Perm.entrySet().iterator(); itr.hasNext();) {
            Map.Entry e = (Map.Entry) itr.next();
            g = (WGroup) e.getKey();
            permGrants = (Collection) e.getValue();
            if (g.getName().equals("Administrators")) break;
        }
        
        assertNotNull(g.getId());
        assertNotNull(g.getName());
        
        assertEquals(4, permGrants.size());
        
        for (Object o : permGrants) {
            pg = (WPermissionGrant) o;
            if (Permission.DELETE_ITEM.equals(pg.getPermission())) {
                assertEquals(WPermissionGrant.REVOKED, pg.getGrant());
            }
        }
    }
    
    public void testGroups() throws Exception {
        WGroup g = new WGroup();
        g.setName("Test Group");
        gwtSecurityService.save(g);
        
        Map<?, ?> groupPermissions = gwtSecurityService.getGroupPermissions();
        
        assertEquals(2, groupPermissions.size());
        
        boolean found = false;
        for (Map.Entry<?, ?>  e: groupPermissions.entrySet()) {
            WGroup wg = (WGroup) e.getKey();
            
            if (wg.getName().equals(g.getName())) {
                found = true;
            }
        }
        assertTrue(found);
    }
}
