package org.mule.galaxy.web.server;

import java.util.Collection;
import java.util.List;

import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.web.client.ArtifactGroup;
import org.mule.galaxy.web.client.RegistryService;

public class RegistryServiceTest extends AbstractGalaxyTest {
    protected RegistryService gwtRegistry;
    
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-web.xml" };
        
    }
    public void testWorkspaces() throws Exception {
        Collection workspaces = gwtRegistry.getWorkspaces();
        assertEquals(1, workspaces.size());
        
        Collection artifactTypes = gwtRegistry.getArtifactTypes();
        assertTrue(artifactTypes.size() > 0);
        
        
        importHelloMule();
        importHelloWsdl();
        
        Collection artifacts = gwtRegistry.getArtifacts(null);
        
        assertEquals(2, artifacts.size());
        
        ArtifactGroup g1 = (ArtifactGroup) artifacts.iterator().next();
        assertEquals("Mule Configuration", g1.getName());
        
        List columns = g1.getColumns();
        assertEquals(4, columns.size());
        
        List rows = g1.getRows();
        assertEquals(1, rows.size());
    }
}
