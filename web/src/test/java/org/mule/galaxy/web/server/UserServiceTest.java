package org.mule.galaxy.web.server;

import java.util.Collection;

import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.rpc.UserService;
import org.mule.galaxy.web.rpc.WUser;

public class UserServiceTest extends AbstractGalaxyTest {
    protected UserService gwtUserService;
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-web.xml",
                              "/META-INF/applicationContext-test.xml" };
        
    }
    public void testWorkspaces() throws Exception {
        Collection users = gwtUserService.getUsers();
        
        assertEquals(1, users.size());
        WUser user = new WUser();
        user.setUsername("dandiep");
        user.setName("Dan Diephouse");
        String id = gwtUserService.addUser(user, "foo");
        
        assertNotNull(id);
    }
}
