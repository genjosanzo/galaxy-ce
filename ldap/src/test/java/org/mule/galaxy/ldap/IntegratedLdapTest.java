package org.mule.galaxy.ldap;

import java.util.Collection;
import java.util.List;

import org.acegisecurity.Authentication;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.acegisecurity.providers.ldap.LdapAuthenticationProvider;
import org.acegisecurity.userdetails.UserDetails;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.Workspace;
import org.mule.galaxy.security.User;
import org.mule.galaxy.security.UserManager;
import org.mule.galaxy.security.ldap.LdapUserManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

/**
 * Tests LDAP + Galaxy.
 */
public class IntegratedLdapTest extends AbstractGalaxyTest {
    
    public void testUserManager() throws Exception {
        UserManager userManager = (UserManager) applicationContext.getBean("userManager");
        assertTrue(userManager instanceof LdapUserManager);
        
        LdapUserManager lUserManager = (LdapUserManager) userManager;
        
        User user = userManager.get("admin");
        assertNotNull(user);
        
        assertEquals(1, user.getGroups().size());
        
        UserDetails details = lUserManager.loadUserByUsername("admin");
        GrantedAuthority[] authorities = details.getAuthorities();
        System.out.println(authorities.length);
        
        assertTrue(authorities.length > 5);
        
        importHelloWsdl();
        
        // do our perms work?
        Workspace w = registry.getWorkspaces().iterator().next();
        Collection<Artifact> artifacts = registry.getArtifacts(w);
        
        assertEquals(1, artifacts.size());
//        
//        List<User> users = userManager.listAll();
//        
//        assertEquals(1, users.size());
    }

    protected String getPassword() {
        return "secret";
    }
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml",
                              "/META-INF/applicationContext-acegi-security.xml",
                              "/META-INF/applicationContext-test.xml",
                              "/META-INF/applicationContext-ldap.xml" };
    }

}
