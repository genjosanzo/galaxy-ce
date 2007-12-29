package org.mule.galaxy.web.server;

import java.util.Collection;

import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.rpc.UserService;

public class UserServiceTest extends AbstractGalaxyTest {
    protected UserService gwtUserService;
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-web.xml" };
        
    }
    public void testWorkspaces() throws Exception {
        Collection users = gwtUserService.getUsers();
        
        assertEquals(1, users.size());
        
        String id = gwtUserService.addUser("dandiep", "Dan Diephouse", "foo");
        
        assertNotNull(id);
    }
}
