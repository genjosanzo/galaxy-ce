package org.mule.galaxy.web.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

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
                              "/META-INF/applicationContext-acegi-security.xml",
                              "/META-INF/applicationContext-web.xml",
                              "/META-INF/applicationContext-test.xml" };
        
    }
    public void testUsers() throws Exception {
        Collection users = gwtSecurityService.getUsers();
        
        assertEquals(1, users.size());
        
        WUser admin = (WUser) users.iterator().next();
        Collection groupIds = admin.getGroupIds();
        assertEquals(2, groupIds.size());
        
        WUser user = new WUser();
        user.setUsername("dandiep");
        user.setName("Dan Diephouse");
        String id = gwtSecurityService.addUser(user, "foo");
        
        assertNotNull(id);
    }

    public void testPermissions() throws Exception {
        Map group2Perm = gwtSecurityService.getGroupPermissions();
        
        assertEquals(2, group2Perm.size());
        
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
    
    public void testGroups() {
        WGroup g = new WGroup();
        g.setName("Test Group");
        gwtSecurityService.save(g);
        
        Map<?, ?> groupPermissions = gwtSecurityService.getGroupPermissions();
        
        assertEquals(3, groupPermissions.size());
        
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
