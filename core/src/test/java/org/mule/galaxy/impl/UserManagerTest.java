package org.mule.galaxy.impl;

import java.util.List;

import org.mule.galaxy.ArtifactType;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class UserManagerTest extends AbstractGalaxyTest {
    public void testDao() throws Exception {
        List<User> users = userManager.listAll();
        
        assertEquals(1, users.size());
        User u = users.get(0);
        assertEquals("admin", u.getUsername());
        assertEquals("Administrator", u.getName());
        assertNotNull(u.getCreated());
        assertNotNull(u.getId());
        
        User user = userManager.create("dan", "dan", "Dan Diephouse");
        assertNotNull(user);
        assertEquals("dan", user.getUsername());
        assertEquals("Dan Diephouse", user.getName());
        assertNotNull(user.getCreated());
        
        users = userManager.listAll();
        assertEquals(2, users.size());
        
        User authUser = userManager.authenticate("admin", "admin");
        assertNotNull(authUser);
        
        userManager.delete(user.getId());
        
        users = userManager.listAll();
        assertEquals(1, users.size());
        
        // we really just disabled the user
        user = userManager.get(user.getId());
        assertNotNull(user);
        
        // make sure we can add another one with the same name as was deleted
        user = userManager.create("dan", "dan", "Dan Diephouse");
        assertNotNull(user);
        assertEquals("dan", user.getUsername());
        assertEquals("Dan Diephouse", user.getName());
        assertNotNull(user.getCreated());
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }

}
