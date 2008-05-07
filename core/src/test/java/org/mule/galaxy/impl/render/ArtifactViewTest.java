package org.mule.galaxy.impl.render;

import org.mule.galaxy.Artifact;
import org.mule.galaxy.impl.render.CustomArtifactRenderer;
import org.mule.galaxy.render.RendererManager;
import org.mule.galaxy.test.AbstractGalaxyTest;
import org.mule.galaxy.util.Constants;

public class ArtifactViewTest extends AbstractGalaxyTest {
    protected RendererManager rendererManager;

    public void testView() throws Exception {
        assertNotNull(rendererManager);
        
        CustomArtifactRenderer view = (CustomArtifactRenderer) rendererManager.getArtifactRenderer(Constants.WSDL_DEFINITION_QNAME);
        
        assertEquals(9, view.getColumns().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();
        
        // Check and see if our view works
        String[] columns = view.getColumnNames();
        assertEquals(9, columns.length);
        
        String name = artifact.getName();
        assertEquals(name, view.getColumnValue(artifact, 0));
        assertEquals("/Default Workspace/", view.getColumnValue(artifact, 1));
        assertEquals("application/xml", view.getColumnValue(artifact, 2));
        assertEquals("http://mule.org/hello_world", view.getColumnValue(artifact, 3));
        assertEquals("0.1", view.getColumnValue(artifact, 4));
        assertEquals("Created", view.getColumnValue(artifact, 5));
        assertEquals("1", view.getColumnValue(artifact, 6));
    }
    
}
