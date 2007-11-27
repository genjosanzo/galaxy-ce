package org.mule.galaxy.view;

import java.util.Collection;

import org.mule.galaxy.AbstractGalaxyTest;
import org.mule.galaxy.Artifact;
import org.mule.galaxy.impl.view.CustomArtifactView;
import org.mule.galaxy.util.Constants;
import org.mule.galaxy.view.ArtifactView;
import org.mule.galaxy.view.ViewLink;

public class ArtifactViewTest extends AbstractGalaxyTest {
    
    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/applicationContext-core.xml", 
                              "/META-INF/applicationContext-test.xml" };
    }
    
    public void testView() throws Exception {
        CustomArtifactView view = new CustomArtifactView();
        view.getColumns().add(new Column("Services", new ColumnEvaluator() {
            public Object getValue(Artifact artifact) {
                Object o = artifact.getLatestVersion().getProperty("wsdl.service");
                
                if (o != null) {
                    return ((Collection) o).size();
                }
                return 0;
            }
        }));
        
        view.getColumns().add(1, new Column("Namespace", new ColumnEvaluator() {
            public Object getValue(Artifact artifact) {
                return artifact.getLatestVersion().getProperty("wsdl.targetNamespace");
            }
        }));
        
        assertEquals(5, view.getColumns().size());
        
        // Import a document which should now be indexed
        Artifact artifact = importHelloWsdl();
        
        // Check and see if our view works
        String[] columns = view.getColumnNames();
        assertEquals(5, columns.length);
        
        String name = artifact.getName();
        assertEquals(name, view.getColumnValue(artifact, 0));
        assertEquals("http://mule.org/hello_world", view.getColumnValue(artifact, 1));
        assertEquals("0.1", view.getColumnValue(artifact, 2));
        assertEquals("", view.getColumnValue(artifact, 3));
        
        assertEquals("1", view.getColumnValue(artifact, 4));
    }
    
}
