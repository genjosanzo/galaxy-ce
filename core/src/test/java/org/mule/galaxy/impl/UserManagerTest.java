package org.mule.galaxy.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserExistsException;
import org.mule.galaxy.test.AbstractGalaxyTest;

public class UserManagerTest extends AbstractGalaxyTest {
    public void testDao() throws Exception {
        List<User> users = userManager.listAll();
        
        assertEquals(1, users.size());
        User u = users.get(0);
        assertEquals("admin", u.getUsername());
        assertEquals("Administrator", u.getName());
        assertNotNull(u.getCreated());
        assertNotNull(u.getId());
        
        User dan = new User();
        dan.setUsername("dan");
        dan.setName("Dan Diephouse");

        HashMap<String, Object> props = new HashMap<String, Object>();
        props.put("foo", "bar");
        props.put("fooList", Arrays.asList("bar"));
        dan.setProperties(props);
        
        userManager.create(dan, "dan");
        
        dan = userManager.get(dan.getId());
        assertNotNull(dan);
        assertNotNull(dan.getId());
        assertEquals("dan", dan.getUsername());
        assertEquals("Dan Diephouse", dan.getName());
        assertNotNull(dan.getCreated());
        assertNotNull(dan.getProperties());
        
        assertEquals("bar", dan.getProperties().get("foo"));
        assertNotNull(dan.getProperties().get("fooList"));
        assertTrue(dan.getProperties().get("fooList") instanceof List);
        assertEquals("bar", ((List)dan.getProperties().get("fooList")).get(0));
        
        users = userManager.listAll();
        assertEquals(2, users.size());
        
        User authUser = userManager.authenticate("admin", "admin");
        assertNotNull(authUser);
        
        userManager.delete(dan.getId());
        
        users = userManager.listAll();
        assertEquals(1, users.size());
        
        // we really just disabled the user
        dan = userManager.get(dan.getId());
        assertNotNull(dan);
        
        // make sure we can add another one with the same name as was deleted
        dan = new User();
        dan.setUsername("dan");
        dan.setName("Dan Diephouse");
        userManager.create(dan, "dan");
        assertNotNull(dan.getId());
        assertEquals("dan", dan.getUsername());
        assertEquals("Dan Diephouse", dan.getName());
        assertNotNull(dan.getCreated());
        
        users = userManager.getUsersForGroup(accessControlManager.getGroupByName("Administrators").getId());
        assertEquals(1, users.size());
    }
    
    public void testUserExistsException() throws Exception {
        User admin = new User();
        admin.setUsername("admin");
        admin.setName("Dan Diephouse");

        try {
            userManager.create(admin, "test");
            fail("User already exists, should've thrown exception!");
        } catch (UserExistsException e) {
        }
    }
}
