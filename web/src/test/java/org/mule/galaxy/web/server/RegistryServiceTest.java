package org.mule.galaxy.web.server;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.acegisecurity.providers.AuthenticationProvider;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.BasicArtifactInfo;
import org.mule.galaxy.web.client.ExtendedArtifactInfo;
import org.mule.galaxy.web.client.RegistryService;
import org.mule.galaxy.web.client.WComment;
import org.springframework.context.ApplicationContext;

public class RegistryServiceTest extends AbstractGalaxyTest {
    protected RegistryService gwtRegistry;
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-acegi-security.xml", 
                              "/META-INF/applicationContext-web.xml" };
        
    }
    
    public void testArtifactOperations() throws Exception {
        Collection workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Collection artifactTypes = gwtRegistry.getArtifactTypes();
        assertTrue(artifactTypes.size() > 0);
        
        Collection artifacts = gwtRegistry.getArtifacts(null, null);
        
        assertEquals(3, artifacts.size());
        
        ArtifactGroup g1 = (ArtifactGroup) artifacts.iterator().next();
        assertEquals("Mule Configuration", g1.getName());
        
        List columns = g1.getColumns();
        assertEquals(4, columns.size());
        
        List rows = g1.getRows();
        assertEquals(1, rows.size());
        
        BasicArtifactInfo a = (BasicArtifactInfo) g1.getRows().get(0);
        Collection deps = gwtRegistry.getDependencyInfo(a.getId());
        assertEquals(0, deps.size());
        
        // Test reretrieving the artifact
        g1 = gwtRegistry.getArtifact(a.getId());
        g1 = (ArtifactGroup) artifacts.iterator().next();
        assertEquals("Mule Configuration", g1.getName());
        
        columns = g1.getColumns();
        assertEquals(4, columns.size());
        
        rows = g1.getRows();
        assertEquals(1, rows.size());
        
        gwtRegistry.setProperty(a.getId(), "location", "Grand Rapids");
        
        Artifact artifact = registry.getArtifact(a.getId());
        assertEquals("Grand Rapids", artifact.getProperty("location"));
        
        // try adding a comment
        createSecureContext(applicationContext, "admin", "admin");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        Object principal = auth.getPrincipal();
        assertNotNull(principal);
        
        WComment wc = gwtRegistry.addComment(a.getId(), null, "Hello World");
        assertNotNull(wc);
        
        // get the extended artifact info again
        g1 = gwtRegistry.getArtifact(a.getId());
        
        rows = g1.getRows();
        ExtendedArtifactInfo ext = (ExtendedArtifactInfo) rows.get(0);
        
        List comments = ext.getComments();
        assertEquals(1, comments.size());
    }
    
    private static void createSecureContext(final ApplicationContext ctx, final String username, final String password) {
        AuthenticationProvider provider = (AuthenticationProvider) ctx.getBean("daoAuthenticationProvider");
        Authentication auth = provider.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    public void testIndexes() throws Exception {
        Map indexes = gwtRegistry.getIndexes();
        
        assertTrue(indexes.size() > 0);
        
    }
}
