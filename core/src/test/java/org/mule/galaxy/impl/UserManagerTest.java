package org.mule.galaxy.impl;

import org.mule.galaxy.security.User;
import org.mule.galaxy.test.AbstractGalaxyTest;

import java.util.List;

public class UserManagerTest extends AbstractGalaxyTest {
    public void testDao() throws Exception {
        List<User> users = userManager.listAll();
        
        assertEquals(1, users.size());
        User u = users.get(0);
        assertEquals("admin", u.getUsername());
        assertEquals("Administrator", u.getName());
        assertNotNull(u.getCreated());
        assertNotNull(u.getId());
        
        User user = new User();
        user.setUsername("dan");
        user.setName("Dan Diephouse");
        
        userManager.create(user, "dan");
        assertNotNull(user);
        assertNotNull(user.getId());
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
        user = new User();
        user.setUsername("dan");
        user.setName("Dan Diephouse");
        userManager.create(user, "dan");
        assertNotNull(user.getId());
        assertEquals("dan", user.getUsername());
        assertEquals("Dan Diephouse", user.getName());
        assertNotNull(user.getCreated());
    }

}
