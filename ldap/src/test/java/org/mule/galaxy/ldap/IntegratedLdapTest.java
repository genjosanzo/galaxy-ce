package org.mule.galaxy.ldap;

import java.util.Collection;
import java.util.List;

import org.apache.xbean.spring.context.ClassPathXmlApplicationContext;
import org.mule.galaxy.Item;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.security.ldap.LdapUserManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.SecurityUtils;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Tests LDAP + Galaxy.
 */
public class IntegratedLdapTest extends AbstractGalaxyTest {
    
    public void testUserManager() throws Exception {
	
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        assertTrue(userManager instanceof LdapUserManager);
//        
        User user = SecurityUtils.getCurrentUser();
        assertEquals(1, user.getGroups().size());
        
        user = userManager.get(user.getId());
        assertNotNull(user);
        
        List<User> users = userManager.listAll();
        assertNotNull(users);
        
        assertEquals(1, users.size());
        
        user = users.iterator().next();
        assertEquals("admin", user.getUsername());
        assertNull(user.getEmail());
        assertEquals("system administrator", user.getName());

        importHelloWsdl();
        
        // do our perms work?
        Workspace w = registry.getWorkspaces().iterator().next();
        Collection<Item> artifacts = w.getItems();
        
        assertEquals(1, artifacts.size());
    }

    protected String getPassword() {
        return "secret";
    }
    
    @Override
    protected ConfigurableApplicationContext createApplicationContext(
	    String[] locations) {
	if (System.getProperty("basedir") == null) {
	    System.setProperty("basedir", "");
	}
	
	// Apache DS uses XBean
	return new ClassPathXmlApplicationContext(locations);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml",
                              "/META-INF/applicationContext-acegi-security.xml",
                              "/META-INF/applicationContext-test.xml",
                              "/META-INF/applicationContext-ldap.xml",
                              "server.xml"};
    }

}
